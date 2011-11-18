<?php

require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");
require_once("generalFunctions.php");

//connect to database
include("connect.php");

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');

if (!isset($_SESSION['courseId']) || !isset($_SESSION['courseInfo']))
{
  $_SESSION['msg']['error'] = 'Course not set.';
  header ("Location: courses.php");
  exit;
}

$courseId = $_SESSION['courseId'];
$courseInfo = $_SESSION['courseInfo'];

// Checks for user parameter
$userId = null;
if((isset($_GET['userId'])) && is_numeric($_GET['userId']))
{
  // Set the userId if user is admin or professor of the course the student is in
  if ($_SESSION['acctType'] == "admin")
  {
    $userId = $_GET['userId'];
  }
  else if ($_SESSION['acctType'] == "professor" && isset($courseId))
  {
    // Check that the user id is in the course specified
    if (isUserInCourse($_GET['userId'], $courseId))
      $userId = $_GET['userId'];
    // Indicate error and redirect
    else
    {
      $_SESSION['msg']['error'] = "Student not enrolled in this course.";
      header ("Location: courseManage.php");
      exit;
    }
  }
}
if ($userId == null && $_SESSION['acctType'] == 'student')
  $userId = $_SESSION['userId'];

// Display current user information
if (($userInfo = getUserInfoById($userId)) !== false)
  $tpl->assign($userInfo);

// Display course info
$tpl->assign('course', $courseInfo);

//addAssignment($courseId, 'Assignment 1', '9/11/2008 12:00am', '9/18/2008 12:00am');
// Get and display assignments
$params = array();
if ($userId !== null)
  $params['userId'] = $userId;
$assignments = getAssignments($courseId, $params);
$tpl->assign('assignments', $assignments);

// Determine who can submit a report for an assignment and who can edit assignments
if ($_SESSION['acctType'] == 'student')
{
  $tpl->assign('submittable', true);
  $tpl->assign('editable', false);
}
else
{
  $tpl->assign('submittable', false);
  $tpl->assign('editable', true);
  
  // Get each student in the course
  if (($students = getCourseStudents($courseId)) !== false)
    $tpl->assign('students', $students);
}

// Assign rand for security
$tpl->assign('rand', md5(session_id()));

// Finish Breadcrumbs
$breadcrumbs[] = array('text' => $courseInfo['section'] . ': ' . $courseInfo['name'], 'href' => 'courses.php?courseId='.$courseId);
$text = 'Dropbox';
if (isset($userInfo) && $userId != $_SESSION['user_id'])
  $text .= ' (' . $userInfo['name'] . ')';
$breadcrumbs[] = array('text' => $text, 'href' => 'dropbox.php?courseId='.$courseId.'&amp;userId='.$userId);
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch('dropbox.tpl');
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
