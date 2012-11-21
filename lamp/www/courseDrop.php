<?php
require_once("loginCheck.php");

// Ensure that the security random number is correct
if (!isset($_GET['rand']) || $_GET['rand'] != md5(session_id()))
{
  $_SESSION['msg']['error'] = "Security Error.";
  header("Location: courses.php");
  exit();
}

$fatal = false;

// Connect to database
include("connect.php");

// Check that course id is valid
if (!isset($_GET['courseId']) || !is_numeric($_GET['courseId']))
{
  if (!isset($_SESSION['msg']['error']))
    $_SESSION['msg']['error'] = "";
  $_SESSION['msg']['error'] .= "Invalid course id.";
  $fatal = true;
}
else
{
  // Check that course id is valid
  if (!courseExists($_GET['courseId']))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Invalid course id.";
    $fatal = true;
  }
}

// Get user id
$userId = $_SESSION['userId'];
if (isset($_GET['userId']) && $_GET['userId'] != $userId)
{
  $userId = $_GET['userId'];

  // Check that the course id is for the current professor
  if ($_SESSION['acctType'] == 'professor')
  {
    $error = true;
    // Get course info
    if ($courseInfo = getCourseInfo($_GET['courseId']))
      if ($courseInfo['profId'] == $_SESSION['userId'])
        $error = false;

    // Indicate error and redirect
    if ($error)
    {
      if (!isset($_SESSION['msg']['error']))
        $_SESSION['msg']['error'] = "";
      $_SESSION['msg']['error'] .= "You cannot edit another user's courses.<br/>";
      $fatal = true;
    }
  }

  if ($_SESSION['acctType'] != 'admin' && $_SESSION['acctType'] != 'professor')
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "You cannot edit another user's courses.<br/>";
    $fatal = true;
  }
  else if (!is_numeric($_GET['userId']))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Invalid user id.<br/>";
    $fatal = true;
  }
  else
  {
    // Check that course id is valid
    if (!userIdExists($_GET['userId']))
    {
      if (!isset($_SESSION['msg']['error']))
        $_SESSION['msg']['error'] = "";
      $_SESSION['msg']['error'] .= "Invalid user id.";
      $fatal = true;
    }
  }
}

if (!$fatal)
{
  // Check that user is enrolled in the course
  if (!isUserInCourse($userId, $_GET['courseId']))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "You are not enrolled in this course.";
    $fatal = true;
  }
}

// Proceed with course registration if no fatal errors
if (!$fatal)
{
  // Try to drop the course
  if (courseDrop($userId, $_GET['courseId']))
  {
    $_SESSION['msg']['success'] = "Successfully dropped the course.";
  }
  else
  {
    /*
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "MySQL Error: " . mysql_error();
    */
  }
}

mysql_close();

$location = (isset($_GET['loc'])) ? $_GET['loc'] : 'courses.php';
header('Location: '.$location);
?>
