<?php
$acctTypes = array('professor', 'admin');
require_once("loginCheck.php");

// Connect to database
include("connect.php");

require_once("mysqlFunctions.php");

/* Get information on the course to be edited and check permissions*/
if (isset($_GET['courseId']))
{
  // Get course info which also checks that the course id is valid
  $courseInfo = getCourseInfo($_GET['courseId'], true);
  if ($courseInfo === false)
  {
    $_SESSION['msg']['error'] = "Invalid course specified.";
    header("Location: courses.php");
    exit;
  }

  // Check that this is this professors course if user is professor
  if ($_SESSION['acctType'] == "professor" && $_SESSION['userId'] != $courseInfo['profId'])
  {
    $_SESSION['msg']['error'] = "You cannot disable any course other than your own.";
    header("Location: courses.php");
    exit;
  }
}
// Indicate error and redirect
else
{
  $_SESSION['msg']['error'] = "Course to be disabled was not specified.";
  header("Location: courses.php");
  exit;
}

// Check if rand is set and correct
if(isset($_GET['rand']) && $_GET['rand'] == md5(session_id()))
{
  // Connect to database
  include("connect.php");

  // Disable the course
  if (setCourseStatus($_GET['courseId'], 'disabled'))
    $_SESSION['msg']['success'] = "The course has been disabled!";
  else
    $_SESSION['msg']['error'] = "There was an error disabling the course.";
}
else
  $_SESSION['msg']['error'] = "Security Error.";

// Redirect
header("Location: courses.php");
exit();
?>
