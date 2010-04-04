<?php
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");

if (!isset($_SESSION['courseId']) || !isset($_SESSION['courseInfo']))
{
	$_SESSION['msg']['error'] = 'Course not set.';
	header('Location: index.php');
	exit;
}

$courseId = (int)$_SESSION['courseId'];
$courseInfo = $_SESSION['courseInfo'];

// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");

// Determine if we are editing an existing assignment
$assignment = null;
$editing = (isset($_GET['state']) && $_GET['state'] == 'edit' && isset($_GET['assignment_id']) && ($assignment = getAssignment($_GET['assignment_id'])) !== false);
if (!empty($assignment))
	$assignmentId = $_GET['assignment_id'];

if (!empty($_POST))
{
	$fatal = false;

	// Ensure that the security random number is correct
	$secure = false;
	require_once("securityFunctions.php");
	if (isset($_POST['securityRand']) && isset($_POST['securityPage']))
		if (checkSecurity($_POST['securityPage'], $_POST['securityRand']) == 1)
			$secure = true;

	if($secure)
	{
		// Check for errors
		$error = array();

		// Check course name field for problems
		if (!isset($_POST['name']) || $_POST['name'] == "")
		{
			$error[] = "Assignment name not entered.";
			$fatal = true;
		}
		else if (strlen($_POST['name']) > 255)
		{
			$error[] = "Assignment name too long, truncating.";
			$_POST['name'] = substr($_POST['name'], 0, 255);
		}

		// Validate and parse open date
		if (!isset($_POST['openDate']) || $_POST['openDate'] == "")
		{
			$error[] = "Assignment open date not fully entered.";
			$fatal = true;
		}
		else
		{
			$openMonth = strtok($_POST['openDate'], "/");
			$openDay = strtok("/");
			$openYear = strtok("/");
		}

		// Validate and contruct open time
		if (!isset($_POST['openTime']) || $_POST['openTime'] == "")
		{
			$error[] = "Assignment open time not fully entered.";
			$fatal = true;
		}
		else
		{
			$openHour = substr($_POST['openTime'], 0, 2);
			$openMinute = substr($_POST['openTime'], 3, 2);
			$openMeridiem = substr($_POST['openTime'], 5, 2);
			if ($openMeridiem == "AM" && $openHour == 12)
				$openHour = 0;
			else if ($openMeridiem == "PM" && $openHour != 12)
				$openHour += 12;

			$openTime = mktime($openHour, $openMinute, 0, $openMonth, $openDay, $openYear);
		}

		// Validate and parse close date
		if (!isset($_POST['closeDate']) || $_POST['closeDate'] == "")
		{
			$error[] = "Assignment close date not fully entered.";
			$fatal = true;
		}
		else
		{
			$closeMonth = strtok($_POST['closeDate'], "/");
			$closeDay = strtok("/");
			$closeYear = strtok("/");
		}

		// Validate and parse close time
		if (!isset($_POST['closeTime']) || $_POST['closeTime'] == "")
		{
			$error[] = "Assignment close time not fully entered.";
			$fatal = true;
		}
		else
		{
			$closeHour = substr($_POST['closeTime'], 0, 2);
			$closeMinute = substr($_POST['closeTime'], 3, 2);
			$closeMeridiem = substr($_POST['closeTime'], 5, 2);
			if ($closeMeridiem == "AM" && $closeHour == 12)
				$closeHour = 0;
			else if ($closeMeridiem == "PM" && $closeHour != 12)
				$closeHour += 12;

			$closeTime = mktime($closeHour, $closeMinute, 0, $closeMonth, $closeDay, $closeYear);
    		}

		// Remove any html codes from posts
		$_POST['name'] = strip_tags($_POST['name']);

		// Add slashes to all fields if needed
		if (!get_magic_quotes_gpc())
			$_POST['name'] = addslashes($_POST['name']);

		// Assign errors
		if (!empty($error))
			$_SESSION['msg']['error'] = implode('<br/>', $error);

		// Proceed if no fatal errors
		if (!$fatal)
		{
			// Determine if we are editing or inserting
			if ($editing)
			{
				// Insert assignment into database
				if ((updateAssignment($assignmentId, $courseId, $_POST['name'], $openTime, $closeTime)) !== false)
				{
					// Set reporting options and report error
					switch (setAssignmentOptions($assignmentId, $_POST['doclets']))
					{
					case 0:
						$_SESSION['msg']['success'] = 'Assignment successfully edited.';
						break;
					default:
						$_SESSION['msg']['error'] = 'There was an error updating the assignment options.';
						break;
					}
				}
				else
				{
					$_SESSION['msg']['error'] = 'There was an error updating the assignment.';
					//$_SESSION['msg']['error'] = "MySQL Error: " . mysql_error();
				}
			}
			else
			{
				// Insert assignment into database
				if ($assignmentId = addAssignment($courseId, $_POST['name'], $openTime, $closeTime))
				{
					// Set reporting options and report error
					switch (setAssignmentOptions($assignmentId, $_POST['doclets']))
					{
						case 0:
							$_SESSION['msg']['success'] = 'Assignment successfully added.';
							break;
						default:
							$_SESSION['msg']['error'] = 'There was an error updating the assignment options.';
							break;
					}
				}
				else
				{
					$_SESSION['msg']['error'] = 'There was an error updating the assignment.';
					//$_SESSION['msg']['error'] = "MySQL Error: " . mysql_error();
				}
			}

			// Add grading options
			if (is_numeric($assignmentId))
			{
				// Ensure that there are no submissions for the assignment yet
				$submissions = numAssignmentSubmissions($assignmentId);
				$changable = true;
				if ($submissions == -1 || $submissions > 0)
					$changable = false;
				// Add or update each section grade
				if (isset($_POST['dgs']) && isset($_POST['dgs_prev']))
				{
					$error = "";
					foreach ($_POST['dgs'] as $dgsId=>$maxGrade)
					{
						// Check if the value changed
						if ($_POST['dgs_prev'][$dgsId] != $maxGrade)
						{
							// Check if the value can be changed
							if (!$changable)
								$error = "You cannot change the grade breakdown.  A report was already submitted using the previous grade breakdown.<br/>";
							else if (!setAssignmentSectionGrade($assignmentId, $dgsId, $maxGrade))
								$error = "Failed to set grade breakdown.<br/>";
						}
					}
					if (!empty($error))
						$_SESSION['msg']['error'] .= $error;
				}
				// Add or update each section parameters
				if (isset($_POST['dgp']) && isset($_POST['dgp_prev']))
				{
					$error = "";
					foreach ($_POST['dgp'] as $dgpId=>$value)
					{
						// Check if the value changed
						if ($_POST['dgp_prev'][$dgpId] != $value)
						{
							// Check if the value can be changed
							if (!$changable)
								$error = "You cannot change the grading parameters.  A report was already submitted using the previous parameter.<br/>";
							else if (!setAssignmentGradeParameters($assignmentId, $dgpId, $value))
								$error = "Failed to set grading parameters.<br/>";
						}
					}
					if (!empty($error))
						$_SESSION['msg']['error'] .= $error;
				}
			}
		}
	}
	else
	{
		$_SESSION['msg']['error'] = 'Security Error.';
	}

	header("Location: dropbox.php");
	exit;
}

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Assign information if editing
$tpl->assign('editing', $editing);
if ($editing)
{
	$tpl->assign('name', $assignment['name']);

	// Parse the database open date entry to a MO/DA/YEAR format
	$openDate = substr($assignment['openTime'], 5, 2) . "/" . substr($assignment['openTime'], 8, 2) . "/" . substr($assignment['openTime'], 0, 4);

	// Get the open hour from the database, which is in 24-hour format
	$openHour = substr($assignment['openTime'], 11, 2);
	// Translate to 12-hour format plus meridiem
	if ($openHour < 12)
	{
		if ($openHour == "00")
			$openHour = "12";
		$meridiem = "AM";
	}
	else
	{
		if ($openHour > 12)
			$openHour -= 12;
		// else open hour is 12 PM

		// Append a leading zero if the number is single-digit (if math is performed on a number, it loses its leading zero(es))
		if ($openHour < 10)
			$openHour = "0" . $openHour;
		$meridiem = "PM";
	}
	// Construct the open time in 12:12AM style format
  	$openTime = $openHour . substr($assignment['openTime'], 13, 3) . $meridiem;

	// Parse the database close date entry to a MO/DA/YEAR format
	$closeDate = substr($assignment['closeTime'], 5, 2) . "/" . substr($assignment['closeTime'], 8, 2) . "/" . substr($assignment['closeTime'], 0, 4);

	// Get the close hour from the database, which is in 24-hour format
	$closeHour = substr($assignment['closeTime'], 11, 2);
	// Translate to 12-hour format plus meridiem
	if ($closeHour < 12)
	{
		if ($closeHour == "00")
			$closeHour = "12";
		$meridiem = "AM";
	}
	else
	{
		if ($closeHour > 12)
			$closeHour -= 12;
		// else open hour is 12 PM

		// Append a leading zero if the number is single-digit (if math is performed on a number, it loses its leading zero(es))
		if ($closeHour < 10)
			$closeHour = "0" . $closeHour;
		$meridiem = "PM";
	}
	// Construct the close time in 12:12AM style format
	$closeTime = $closeHour . substr($assignment['closeTime'], 13, 3) . $meridiem;

	// Get and assign current reporting options
	if ($options = getAssignmentOptions($assignmentId))
		$tpl->assign('assignmentOptions', $options);

	$submissions = numAssignmentSubmissions($_GET['assignment_id']);
	$tpl->assign("gradeInputsEnabled", ($submissions == 0));
}
else	// Not editing, so create the dates and times to prefill the textboxes with
{
	$month = date("m");
	$leapYear = date("L");
	$sevenDaysFromNow = date("d") + 7;

	// Default close date is one week from today; adjust for a month changeover
	if (($sevenDaysFromNow > 31) && ($month == 1 || $month == 3 || $month == 5 || $month == 7 || $month == 8 || $month == 10 || $month == 12))
	{
		$sevenDaysFromNow -= 31;
	}
	else if (($sevenDaysFromNow > 30) && ($month == 4 || $month == 6 || $month == 9 || $month == 11))
	{
		$sevenDaysFromNow -= 30;
	}
	else if (($sevenDaysFromNow > 28) && ($month == 2) && $leapYear == 0)
	{
		$sevenDaysFromNow -= 28;
	}
	else if (($sevenDaysFromNow > 29) && ($month == 2) && $leapYear == 1)
	{
		$sevenDaysFromNow -= 29;
	}

	// Append a leading 0 to match the format if the number is single-digit
	if ($sevenDaysFromNow < 10)
		$sevenDaysFromNow = "0" . $sevenDaysFromNow;

	// Construct a MO/DA/YEAR format date for both the open and close dates
	// Open date is today
	$openDate = date("m/d/Y");
	// Close date is one week from today
	$closeDate = $month . "/" . $sevenDaysFromNow . date("/Y");

	// Increase current minutes by 5 then round down to the nearest number that ends in 0 or 5
	// If this would change the hour, just round down to 55
	$minutes = date("i");
	if ($minutes < 55)
	{
		$minutes = $minutes + 5 - ($minutes % 5);
	}
	else
	{
		$minutes = 55;
	}

	// Append a leading 0 to match the format if the number is single-digit
	if ($minutes < 10)
		$minutes = "0" . $minutes;

	// Construct a 12:12AM style format time for both the open and close times
	$openTime = date("h:") . $minutes . date("A");
	$closeTime = $openTime;
}

$tpl->assign('openDate', $openDate);
$tpl->assign('openTime', $openTime);

$tpl->assign('closeDate', $closeDate);
$tpl->assign('closeTime', $closeTime);

// Get report types (doclets) from database
$params = array(
	'GetGradeSections'=>true,
	'GetGradingParams'=>true,
);
if (($doclets = getDoclets($params)) !== false)
	$tpl->assign('doclets', $doclets);

// Get grading information for assignment
getAssignmentGradingInfo($_GET['assignment_id'], $gradeSections, $gradingParams);

// Get security fields
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'COMTOR', 'href' => 'index.php');
$breadcrumbs[] = array('text' => $courseInfo['section'] . ': ' . $courseInfo['name'], 'href' => 'courses.php?courseId='.$courseId);
$breadcrumbs[] = array('text' => 'Dropbox', 'href' => 'dropbox.php');
$breadcrumbs[] = array('text' => 'Add Assignment', 'href' => 'assignment_add.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Register function to determine input values
$tpl->register_function('gradeSectionInputValue', 'gradeSectionInputValue');
$tpl->register_function('gradeParameterInputValue', 'gradeParameterInputValue');

// Fetch template
$tpldata = $tpl->fetch('assignment_add.tpl');
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

function gradeSectionInputValue($params, &$tpl)
{
	if(empty($params['docletGradeSectionId']))
		return "";
	else
	{
		global $gradeSections;
		return $gradeSections[$params['docletGradeSectionId']];
	}
}

function gradeParameterInputValue($params, &$tpl)
{
	if(empty($params['docletGradeParameterId']))
		return "";
	else
	{
		global $gradingParams;
		return $gradingParams[$params['docletGradeParameterId']];
	}
}

?>
