<?php

require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Get and validate course id from GET parameters
if (isset($_GET['courseId']) && is_numeric($_GET['courseId']) && courseExists($_GET['courseId']) && (($course = getCourseInfo($_GET['courseId'])) !== false))
{
  $courseId = $_GET['courseId'];

  // Determine if the user is admin or the professor of course
  if ($_SESSION['acctType'] == "admin")
  {}
  else if ($_SESSION['acctType'] == "professor")
  {
    // Check that the course id is for the given professor
    if ($course['profId'] != $_SESSION['userId'])
    {
      $_SESSION['msg']['error'] = "You are not professor for this course.";
      header ("Location: courseManage.php");
      exit;
    }
  }
  // Check if student is in course
  else if (!isUserInCourse($_SESSION['userId'], $courseId))
  {
    $_SESSION['msg']['error'] = "You are not enrolled in this course.";
    header ("Location: courses.php");
    exit;
  }
}
else
{
  $_SESSION['msg']['error'] = 'Specified course invalid.';
  header('Location: courses.php');
  exit;
}

// Submit data
if (!empty($_POST))
{
  // Check for errors
  $error = array();

  // Ensure that the security random number is correct
  $secure = false;
  require_once("securityFunctions.php");
  if (isset($_POST['securityRand']) && isset($_POST['securityPage']))
    if (checkSecurity($_POST['securityPage'], $_POST['securityRand']) == 1)
      $secure = true;

  if (!$secure)
  {
    header('Location: index.php');
    exit;
  }

  // Check that recipients were specified
  if (!isset($_POST['recipients']) || !is_array($_POST['recipients']) || count($_POST['recipients']) == 0)
    $error[] = 'Please specify at least one recipient.';
  // Check that recipients are in the course
  else
  {
    // Assign recipients to template in case of error
    $tpl->assign('recipients', $_POST['recipients']);

    $in_course = true;   // Are all of the recipients in the course

    // Check each user
    foreach ($_POST['recipients'] as $recip)
      if(!isUserInCourse($recip, $courseId) && $recip != $course['profId'])
        $in_course = false;

    // Indicate error if needed
    if (!$in_course)
      $error[] = 'All recipients must be in the course.';
  }

  // Check that subject was specified
  if (!isset($_POST['subject']) || !is_string($_POST['subject']))
    $error[] = 'Please specify E-mail subject.';
  else
    // Assign subject to template in case of error
    $tpl->assign('subject', htmlspecialchars($_POST['subject']));

  // Check that content was specified
  if (!isset($_POST['contents']) || !is_string($_POST['contents']) || trim($_POST['contents']) == '')
    $error[] = 'Please specify E-mail content.';
  else
    // Assign content to template in case of error
    $tpl->assign('contents', htmlspecialchars($_POST['contents']));

  // Determine if there was an error
  if (!empty($error))
    $tpl->assign('error', implode('<br/>', $error));
  else
  {
    require_once('generalFunctions.php');

    // Get sender e-mail address
    if (($user = getUserInfoById($_SESSION['userId'], array('name', 'email'))) !== false)
      $from = $user['name'] . ' <' . $user['email'] . '>';

    // Get recipient e-mail addresses
    $recipients = array();
    foreach ($_POST['recipients'] as $recip)
      if (($user = getUserInfoById($recip, array('name', 'email'))) !== false)
        $recipients[] = $user['name'] . ' <' . $user['email'] . '>';

    // Determine e-mail contents
    $contents = nl2br(htmlspecialchars($_POST['contents']));

    $result = sendMail($contents, $recipients, $from, $_POST['subject'], true);

    if (!PEAR::isError($result))
    {
      // Indicate success and redirect
      $_SESSION['msg']['success'] = 'E-Mail Sent.';
      header('Location: index.php');
      exit;
    }
    else
      $tpl->assign('success', 'Unknown error sending E-Mail.');
  }
}

// Get security fields
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Get the name of the professor
$prof = getUserInfoById($course['profId'], array('name'));
if ($prof !== false)
  $course['profName'] = $prof['name'];

// Assign course info
$tpl->assign('courseId', $courseId);
$tpl->assign('course', $course);

// List student accounts in the the given course
$where = 'userId IN (SELECT studentId FROM enrollments WHERE courseId=' . $courseId . ')';
if ($users = getUsers(array("userId", "name"), "enabled", "student", false, false, null, $where))
  $tpl->assign('users', $users);

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$text = (isset($course['section']) && isset($course['name'])) ? $course['section'].': '.$course['name'] : 'Course #'.$courseId;
$breadcrumbs[] = array('text' => $text, 'href' => 'courseManage.php?courseId='.$courseId);
$breadcrumbs[] = array('text' => 'E-Mail', 'href' => 'course_email.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch('email.tpl');
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
