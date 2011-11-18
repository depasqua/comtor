<?php
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

//Define the default and maximum number of courses to be shown
define("DEFAULT_LIST_TOTAL", 25);
define("MAX_LIST_TOTAL", 150);

// Connect to database
include("connect.php");

$tpl->assign('rand', md5(session_id()));

// Check if there is a limit set.
if (!isset($_GET['lower']) || !is_numeric($_GET['lower']))
  $_GET['lower'] = 0;
if (!isset($_GET['total']) || !is_numeric($_GET['total']))
  $_GET['total'] = DEFAULT_LIST_TOTAL;
// Check that total is less than the maximum valid
else if ($_GET['total'] > MAX_LIST_TOTAL)
  $_GET['total'] = MAX_LIST_TOTAL;

// Determine course status values to show
$statusHanding = array(null);
if ($_SESSION['acctType'] == 'professor' || $_SESSION['acctType'] == 'admin')
  $statusHanding = array('enabled', 'disabled');

foreach ($statusHanding as $status)
{
  // Get professor courses
  if ($_SESSION['acctType'] == 'professor')
    $courses = getProfCourses($_SESSION['userId'], $status);
  // Get and show all courses
  else
    $courses = getCourses(false, $_GET['lower'], $_GET['total'], $status);

  if ($courses !== false)
  {
    foreach ($courses as &$course)
    {
      // Get professors name
      if ($_SESSION['acctType'] == 'professor')
        $course['profName'] = $_SESSION['username'];
      else
      {
        $course['profName'] = "User #{$course['profId']}";  // Default name
        if ($prof = getUserInfoById($course['profId'], array("name")))
          $course['profName'] = $prof['name'];
      }

      // Determine if the user is already enrolled in the course
      if ($_SESSION['acctType'] == "student")
      {
        $enrolled = false;
        // Uses $userCourses defined in header.php
        for ($i = 0; $i < count($userCourses) && !$enrolled; $i++)
          if ($userCourses[$i]['courseId'] == $course['courseId'])
            $enrolled = true;
      }

      $course['comment'] = nl2br(htmlspecialchars($course['comment']));

      // Actions
      $actions = array();
      if ($_SESSION['acctType'] == "professor" || $_SESSION['acctType'] == "admin")
      {
        $actions[] = 'edit';
        $actions[] = 'manage';
        if ($course['status'] == 'enabled')
          $actions[] = 'disable';
        if ($course['status'] == 'disabled')
          $actions[] = 'enable';
      }
      if ($_SESSION['acctType'] == "admin")
        $actions[] = 'delete';

      // Show enroll/drop if the user is a student
      if ($_SESSION['acctType'] == "student")
      {
        if ($enrolled)
        {
          $actions[] = 'reports';
          $actions[] = 'drop';
        }
        else
          $actions[] = 'enroll';
      }

      $course['actions'] = $actions;
    }

    // Assign courses
    if ($status == null)
      $tpl->assign('courses_all', $courses);
    else
    {
      require_once("generalFunctions.php");
      $numCourses = getNumCourses(array(), $status);
      $tpl->assign('pages_'.$status, listPages($_GET['lower'], $_GET['total'], $numCourses));
      $tpl->assign('courses_'.$status, $courses);
    }
  }
}

mysql_close();

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Courses', 'href' => 'courses.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("course_manage_all.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
