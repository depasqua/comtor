<?php

$acctTypes = array("student", "professor", "admin");
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");
require_once("mysqlFunctions.php");

// Determine userId to edit
$userId = $_SESSION['userId'];

// Get user id and redirect on unauthorized access
if (isset($_GET['userId']))
{
  if ($_SESSION['acctType'] == 'admin')
    $userId = $_GET['userId'];
  // Redirect
  else if ($_GET['userId'] != $_SESSION['userId'])
  {
    $_SESSION['msg']['error'] = "Unauthorized Access.";
    header("Location: index.php");
    exit;
  }
}

// Determine notification types available
$notify_types = array();
if ($_SESSION['acctType'] == 'student')
{
  $notify_types[NOTIFY_ASSIGNMENT_NEW] = 'New Assignment';
  $notify_types[NOTIFY_ASSIGNMENT_DEADLINE_NEAR] = 'Assignment Deadline Near';
  $notify_types[NOTIFY_ASSIGNMENT_OPEN] = 'Assignment Dropbox Opened';
  $notify_types[NOTIFY_ASSIGNMENT_CLOSE] = 'Assignment Dropbox Closed';
  $notify_types[NOTIFY_ASSIGNMENT_COMMENT] = 'Submission Comment Added';
}
else if ($_SESSION['acctType'] == 'professor')
{
  $notify_types[NOTIFY_STUDENT_SUBMISSION] = 'New Student Submission';
  $notify_types[NOTIFY_ROSTER_CHANGE] = 'Student Enrolled/Dropped Course';
}
else if ($_SESSION['acctType'] == 'admin')
{
  $notify_types[NOTIFY_REQUEST] = 'New Request';
}
$tpl->assign('notify_types', $notify_types);

// Determine notification frequencies available
$notify_frequencies = array();
$notify_frequencies[NOTIFY_FREQ_ON_ACTION] = 'Immediately';
$notify_frequencies[NOTIFY_FREQ_ON_HALF_HOUR] = 'Every Half Hour';
$notify_frequencies[NOTIFY_FREQ_ON_HOUR] = 'Every Hour';
$notify_frequencies[NOTIFY_FREQ_ON_SIX_HOUR] = 'Every 6 Hours';
$notify_frequencies[NOTIFY_FREQ_ON_DAY] = 'At Midnight';
$tpl->assign('notify_frequencies', $notify_frequencies);

// Check for posted data
if (!empty($_POST))
{
  // Ensure that the security random number is correct
  $secure = false;
  require_once("securityFunctions.php");
  if (isset($_POST['securityRand']) && isset($_POST['securityPage']))
    if (checkSecurity($_POST['securityPage'], $_POST['securityRand']) == 1)
      $secure = true;

  // Redirect if not secure
  if (!$secure)
  {
    $_SESSION['msg']['error'] = 'Security Error<br/> The data you submitted was not updated because if a missing or incorrect security feature.  This may be due to resubmiting a form.  Please try again.';
    header('Location: editAccount.php');
    exit;
  }

  $error = array();

  // Check fields
  if (!isset($_POST['frequency']))
    $error[] = 'Notification frequency not sent.';
  else if (!in_array($_POST['frequency'], array_keys($notify_frequencies)))
    $error[] = 'Notification frequency invalid.';

  // Assign errors on exit
  if (!empty($error))
    $tpl->assign('error', implode('<br/>', $error));
  else
  {
    // Get bitwise notify types
    $types = 0;
    if (is_array($_POST['notify_types']))
      foreach ($_POST['notify_types'] as $type)
        $types |= $type;

    // Set options
    switch (setNotifyOptions($userId, $types, $_POST['frequency']))
    {
      case 0:
        $_SESSION['msg']['success'] = 'Notification options updated.';
        header('Location: email_notify_edit.php');
        exit;
        break;
      default:
        $tpl->assign('error', 'Error updating notification options.');
        break;
    }
  }
}

// Get users settings
if ($info = getNotifyOptions($userId))
{
  $tpl->assign('user_notify_types', $info['notifications']);
  $tpl->assign('user_freq', $info['frequency']);
}

// Get security fields
$tpl->assign('get_rand', md5(session_id()));
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$text = 'E-Mail Notifications';
$breadcrumbs[] = array('text' => $text, 'href' => 'email_notify_edit.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
$tooltips = file_get_contents('tooltips/notificationSettings.html');
$tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("email_notify_edit.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
