<?
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

if (!$fatal)
{
  // Check that user is enrolled in the course
  if (!isUserInCourse($_SESSION['userId'], $_GET['courseId']))
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
  if (courseDrop($_SESSION['userId'], $_GET['courseId']))
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

header("Location: courses.php");
?>
