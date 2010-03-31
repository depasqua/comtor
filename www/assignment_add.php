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
    if (!isset($_POST['openDate']))
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
    if (!isset($_POST['openTime']))
    {
      $error[] = "Assignment open time not fully entered.";
      $fatal = true;
    }
    else
    {
      $openHour = substr($_POST['openTime'], 0, 2);
      $openMinute = substr($_POST['openTime'], 3, 2);
      $openMeridian = substr($_POST['openTime'], 5, 2);
      if ($openMeridian == 'AM' && $openHour == 12)
        $openHour = 0;
      else if ($openMeridian == 'PM' && $openHour != 12)
        $openHour += 12;

      $openTime = mktime($openHour, $openMinute, 0, $openMonth, $openDay, $openYear);
    }

    // Validate and parse close date
    if (!isset($_POST['closeDate']))
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
    if (!isset($_POST['closeTime']))
    {
      $error[] = "Assignment close time not fully entered.";
      $fatal = true;
    }
    else
    {
      $closeHour = substr($_POST['closeTime'], 0, 2);
      $closeMinute = substr($_POST['closeTime'], 3, 2);
      $closeMeridian = substr($_POST['closeTime'], 5, 2);
      if ($closeMeridian == 'AM' && $closeHour == 12)
        $closeHour = 0;
      else if ($closeMeridian == 'PM' && $closeHour != 12)
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
