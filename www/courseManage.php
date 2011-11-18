<?php
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");
require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");


// Connect to database
include("connect.php");

// Are we showing a particular course or all courses
$oneCourse = false;

if (isset($_GET['courseId']) && is_numeric($_GET['courseId']))
{
  // Get course info
  $course = getCourseInfo($_GET['courseId']);

  // Check that the id is for the given professor
  if ($course && ($course['profId'] == $_SESSION['userId'] || $_SESSION['acctType'] == "admin"))
  {
    $oneCourse = true;

    /* Display course info if any */
    if (($courseInfo = getCourseInfo($_GET['courseId'])) !== false)
    {
      // Determine professor name
      if ($_SESSION['acctType'] != "professor")
        $courseInfo['profName'] = getUserNameById($courseInfo['profId']);

      // Get each student in the course
      if (($students = getCourseStudents($_GET['courseId'])) !== false)
      {
        foreach ($students as &$student)
        {
          // Get array of student reports
          $reports = getUserReports($student['userId'], $_GET['courseId']);
          $student['submissions'] = ($reports !== false) ? count($reports) : 0;
        }
        unset($student);
        $tpl->assign('students', $students);
      }

      $tpl->assign('courseId', $_GET['courseId']);
      $tpl->assign('course', $courseInfo);
    }
  }

  // Close database connection
  mysql_close();
}

// Assign rand
$tpl->assign('rand', md5(session_id()));

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Courses', 'href' => 'courses.php');
$text = (isset($course['section']) && isset($course['name'])) ? $course['section'].': '.$course['name'] : 'Course #'.$courseId;
$breadcrumbs[] = array('text' => $text, 'href' => 'courseManage.php?courseId='.$courseId);
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("course_manage.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
