<?
$acctTypes = "admin";
require_once("loginCheck.php");

function redirect()
{
  header("Location: courses.php");
  exit();
}

// Ensure that the security random number is correct
if (!isset($_GET['rand']) || $_GET['rand'] != md5(session_id()))
{
  $_SESSION['msg']['error'] = "Security Error.";
  redirect();
}

// Connect to database
include("connect.php");

// Check course id field for problems
if (isset($_GET['course']) && is_numeric($_GET['course']))
{
  // Check that the course id is valid
  if (!courseExists($_GET['course']))
  {
    $_SESSION['msg']['error'] = "Invalid course specified.";
    redirect();
  }
}
else
{
  $_SESSION['msg']['error'] = "Course to be deleted was not specifed.";
  redirect();
}

// Delete course
if (deleteCourse($_GET['course']))
{
  $_SESSION['msg']['success'] = "Course successfully deleted.";
}
else
{
  /*
  if (!isset($_SESSION['msg']['error']))
    $_SESSION['msg']['error'] = "";
  $_SESSION['msg']['error'] .= "MySQL Error: " . mysql_error();
  */
}

mysql_close();

header("Location: courses.php");
?>
