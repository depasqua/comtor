<?
require_once("loginCheck.php");

$fatal = false;

// Connect to database
include("connect.php");

// Check that course id is valid
if (!isset($_GET['id']) || !is_numeric($_GET['id']))
{
  if (!isset($_SESSION['msg']['error']))
    $_SESSION['msg']['error'] = "";
  $_SESSION['msg']['error'] .= "Invalid course id.";
  $fatal = true;
}
else
{
  // Check that course id is valid
  if (!courseExists($_GET['id']))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Invalid course id.";
    $fatal = true;
  }
}

if (!$fatal)
{
  // Check that user is not already enrolled
  if (isUserInCourse($_SESSION['userID'], $_GET['id']))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "You are already enrolled in this course.";
    $fatal = true;
  }
}

// Proceed with course registration if no fatal errors
if (!$fatal)
{
  // Create query to insert course
  if (courseEnroll($_SESSION['userID'], $_GET['id']))
  {
    $_SESSION['msg']['success'] = "Successfully enrolled in course.";
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
