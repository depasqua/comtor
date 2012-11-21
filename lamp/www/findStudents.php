<?php
$acctTypes = array('admin', 'professor');
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Get and validate course id from GET parameters
if (isset($_GET['courseId']) && is_numeric($_GET['courseId']) && courseExists($_GET['courseId']))
{
  $courseId = $_GET['courseId'];

  // Check that the course id is for the current professor
  if ($_SESSION['acctType'] == 'professor')
  {
    $error = true;
    // Get course info
    if ($courseInfo = getCourseInfo($courseId))
      if ($courseInfo['profId'] == $_SESSION['userId'])
        $error = false;

    // Indicate error and redirect
    if ($error)
    {
      $_SESSION['msg']['error'] = "You cannot edit another user's courses.";
      header('Location: courses.php');
      exit;
    }
  }
}
else
{
  $_SESSION['msg']['error'] = 'Specified course invalid.';
  header('Location: courses.php');
  exit;
}

// Get course info
$course = getCourseInfo($courseId);
$tpl->assign('courseId', $courseId);
$tpl->assign('course', $course);

//Define the default and maximum number of users to be shown
define("DEFAULT_LIST_TOTAL", 25);
define("MAX_LIST_TOTAL", 100);

// Connect to database
include("connect.php");

// Random string added to important links for security
$md5Rand = md5(session_id());
$tpl->assign('rand', $md5Rand);

// Check if there is a limit set for enabled accounts
if (!isset($_GET['lowerEn']) || !is_numeric($_GET['lowerEn']))
  $_GET['lowerEn'] = 0;
if (!isset($_GET['totalEn']) || !is_numeric($_GET['totalEn']))
  $_GET['totalEn'] = DEFAULT_LIST_TOTAL;
// Check that total is less than the maximum valid
else if ($_GET['totalEn'] > MAX_LIST_TOTAL)
  $_GET['totalEn'] = MAX_LIST_TOTAL;

// Check if there is a limit set for disabled accounts
if (!isset($_GET['lowerDis']) || !is_numeric($_GET['lowerDis']))
  $_GET['lowerDis'] = 0;
if (!isset($_GET['totalDis']) || !is_numeric($_GET['totalDis']))
  $_GET['totalDis'] = DEFAULT_LIST_TOTAL;
// Check that total is less than the maximum valid
else if ($_GET['totalDis'] > MAX_LIST_TOTAL)
  $_GET['totalDis'] = MAX_LIST_TOTAL;

require_once("generalFunctions.php");

// List student accounts in the same school that are not already in the given course
$where = 'userId NOT IN (SELECT studentId FROM enrollments WHERE courseId=' . $courseId . ')';
if ($users = getUsers(array("userId", "name"), "enabled", "student", $_GET['lowerEn'], $_GET['totalEn'], $_SESSION['schoolId'], $where))
  $tpl->assign('users', $users);

// Add links to view other courses if there are any
$numUsers = getNumUsers("enabled");
$tpl->assign('usersLinks', listPages($_GET['lowerEn'], $_GET['totalEn'], $numUsers, "lowerEn", "totalEn", "lowerDis={$_GET['lowerDis']}&amp;totalDis={$_GET['totalDis']}"));

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Courses', 'href' => 'courses.php');
$text = (isset($course['section']) && isset($course['name'])) ? $course['section'].': '.$course['name'] : 'Course #'.$courseId;
$breadcrumbs[] = array('text' => $text, 'href' => 'courseManage.php?courseId='.$courseId);
$breadcrumbs[] = array('text' => 'Enroll Students', 'href' => 'findStudents.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("course_add_students.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
