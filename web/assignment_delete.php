<?
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");

if (!isset($_SESSION['courseId']) || !isset($_SESSION['courseInfo']))
{
  $_SESSION['msg']['error'] = 'Course not set.';
  header('Location: index.php');
  exit;
}

$courseId = $_SESSION['courseId'];
$courseInfo = $_SESSION['courseInfo'];

// Ensure that the security random number is correct
if (!isset($_GET['rand']) || $_GET['rand'] != md5(session_id()))
{
  $_SESSION['msg']['error'] = "Security Error.";
  header("Location: dropbox.php");
  exit;
}
else
{
  require_once("connect.php");
  require_once("mysqlFunctions.php");

  // Get assignment info
  $assignment = null;
  if (isset($_GET['assignment_id']))
    $assignment = getAssignment($_GET['assignment_id']);

  // Check that the assignment was found and that it has the same course id as
  // the session.  If it does, we know that the user is allowed to delete it.
  if(empty($assignment) || $assignment['courseId'] != $courseId)
  {
    $_SESSION['msg']['error'] = "Assignment not found.";
    header("Location: dropbox.php");
    exit;
  }

  // Delete the assignment
  if (deleteAssignment($assignment['assignmentId']))
    $_SESSION['msg']['success'] = 'Assignment successfully deleted.';
  else
  {
    //$_SESSION['msg']['error'] = "MySQL Error: " . mysql_error();
  }
}

// Redirect
header("Location: dropbox.php");
exit;


?>
