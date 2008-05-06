<?
$acctTypes = "admin";
require_once("loginCheck.php");

// Ensure that the security random number is correct
if (!isset($_GET['rand']) || $_GET['rand'] != md5(session_id()))
{
  $_SESSION['msg']['error'] = "Security Error.";
  header("Location: reports.php");
  exit();
}


// Connect to database
include("connect.php");

// Check that userEventId is valid
if (!isset($_GET['userEventId']) || !is_numeric($_GET['userEventId']))
{
  if (!isset($_SESSION['msg']['error']))
    $_SESSION['msg']['error'] = "";
  $_SESSION['msg']['error'] .= "Invalid user event id.";
}
// Try to delete user event
else
{
  if (deleteUserEvent($_GET['userEventId']))
    $_SESSION['msg']['success'] = "Successfully deleted the user event.";
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

header("Location: reports.php");
?>
