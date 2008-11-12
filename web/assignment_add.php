<?
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

    // Validate and contruct open date
    if (
      !isset($_POST['Open_Month']) || !is_int((int)$_POST['Open_Month']) ||
      !isset($_POST['Open_Day']) || !is_int((int)$_POST['Open_Day']) ||
      !isset($_POST['Open_Year']) || !is_int((int)$_POST['Open_Year']) ||
      !isset($_POST['Open_Hour']) || !is_int((int)$_POST['Open_Hour']) ||
      !isset($_POST['Open_Minute']) || !is_int((int)$_POST['Open_Minute']) ||
      !isset($_POST['Open_Meridian']) || ($_POST['Open_Meridian'] != 'am' && $_POST['Open_Meridian'] != 'pm')
    )
    {
      $error[] = "Assignment open date not fully entered.";
      $fatal = true;
    }
    else
    {
      // Adjust hour for meridian
      $hour = $_POST['Open_Hour'];
      if ($_POST['Open_Meridian'] == 'am' && $hour == 12)
        $hour = 0;
      else if ($_POST['Open_Meridian'] == 'pm' && $hour != 12)
        $hour += 12;

      $openTime = mktime($hour, $_POST['Open_Minute'], 0, $_POST['Open_Month'], $_POST['Open_Day'], $_POST['Open_Year']);
    }

    // Validate and contruct close date
    if (
      !isset($_POST['Close_Month']) || !is_int((int)$_POST['Close_Month']) ||
      !isset($_POST['Close_Day']) || !is_int((int)$_POST['Close_Day']) ||
      !isset($_POST['Close_Year']) || !is_int((int)$_POST['Close_Year']) ||
      !isset($_POST['Close_Hour']) || !is_int((int)$_POST['Close_Hour']) ||
      !isset($_POST['Close_Minute']) || !is_int((int)$_POST['Close_Minute']) ||
      !isset($_POST['Close_Meridian']) || ($_POST['Close_Meridian'] != 'am' && $_POST['Close_Meridian'] != 'pm')
    )
    {
      $error[] = "Assignment close date not fully entered.";
      $fatal = true;
    }
    else
    {
      // Adjust hour for meridian
      $hour = $_POST['Close_Hour'];
      if ($_POST['Close_Meridian'] == 'am' && $hour == 12)
        $hour = 0;
      else if ($_POST['Close_Meridian'] == 'pm' && $hour != 12)
        $hour += 12;

      $closeTime = mktime($hour, $_POST['Close_Minute'], 0, $_POST['Close_Month'], $_POST['Close_Day'], $_POST['Close_Year']);
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

  // Open and close times
  $tpl->assign('openTime', $assignment['openTime']);
  $tpl->assign('closeTime', $assignment['closeTime']);

  // Get and assign current reporting options
  if ($options = getAssignmentOptions($assignmentId))
    $tpl->assign('assignmentOptions', $options);

  $submissions = numAssignmentSubmissions($_GET['assignment_id']);
  $tpl->assign("gradeInputsEnabled", ($submissions == 0));

}

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
