<?php
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

// Check that userEventId is set
if (!isset($_GET['userEventId']))
{
  $_SESSION['msg']['error'] = 'User event not set.';
  header('Location: index.php');
  exit;
}

// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");

// Check that the userEventId was submitted for the current course
if (!isReportForCourseAssignment($_GET['userEventId'], $courseId))
{
  $_SESSION['msg']['error'] = 'User event is not for the current course.';
  header('Location: index.php');
  exit;
}

// Get comment
$comment = getUserEventComment($_GET['userEventId'], false);

if (!empty($_POST))
{
  // Ensure that the security random number is correct
  $secure = false;
  require_once("securityFunctions.php");
  if (isset($_POST['securityRand']) && isset($_POST['securityPage']))
    if (checkSecurity($_POST['securityPage'], $_POST['securityRand']) == 1)
      $secure = true;

  if($secure)
  {
    // Check for errors
    $error = array();

    // Check course name field for problems
    if (!isset($_POST['comment']))
      $error[] = "Comment not entered.";

    // Assign errors
    if (!empty($error))
    {
      $_SESSION['msg']['error'] = implode('<br/>', $error);
    }
    else
    {
      $comment = $_POST['comment'];

      // Insert comment into database
      switch (setUserEventComment(89, $comment))
      {
        case 0:
          $_SESSION['msg']['success'] = 'Comment successfully set.';
          break;
        default:
          $_SESSION['msg']['error'] = 'There was an error setting the comment.';
          break;
      }
    }
  }
  else
  {
    $_SESSION['msg']['error'] = 'Security Error.';
  }

  if (isset($_POST['loc']))
  header('Location: '.$_POST['loc']);
  else
    header('Location: dropbox.php');
  exit;
}

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Assign information
$tpl->assign('comment', ($comment) ? $comment : '');


// Get security fields
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => $courseInfo['section'] . ': ' . $courseInfo['name'], 'href' => 'courses.php?courseId='.$courseId);
$breadcrumbs[] = array('text' => 'Dropbox', 'href' => isset($_GET['loc']) ? $_GET['loc'] : 'dropbox.php');
$breadcrumbs[] = array('text' => 'Submission Comment', 'href' => 'assignment_comment.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch('assignment_comment.tpl');
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
