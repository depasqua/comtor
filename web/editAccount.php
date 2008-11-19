<?php
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

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

// Assign user id to template
$tpl->assign('userId', $userId);

// Get possible account types
if ($_SESSION['acctType'] == 'admin')
{
  $acctTypes = getUserAcctTypes();
  $tpl->assign('acctTypes', $acctTypes);
}

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
  if (!isset($_POST['name']))
    $error[] = 'Name field not sent.';
  else
  {
    $name = trim($_POST['name']);
    if (empty($name))
      $error[] = 'Name is required.';
  }

  // School
  if (!isset($_POST['school']))
    $error[] = 'School field not sent.';
  else
  {
    $school = $_POST['school'];
    if (!schoolExists($school))
      $error[] = 'School is required.';
  }

  // Check additional fields if admin
  $email = $acctType = null;
  if ($_SESSION['acctType'] == 'admin')
  {
    // Check e-mail
    if (!isset($_POST['email']))
      $error[] = 'E-mail address field not sent.';
    else
    {
      $email = trim($_POST['email']);
      if (empty($email))
        $error[] = 'E-mail address is required.';

      // Check that address is unique
      if (emailExists($email, $userId))
        $error[] = 'E-mail address already used.';
    }

    // Check account types
    if (!isset($_POST['acctType']))
      $error[] = 'Account type field not sent.';
    else
    {
      $acctType = trim($_POST['acctType']);
      if (!in_array($acctType, $acctTypes))
        $error[] = 'Account type is invalid.';
    }
  }

  // Assign errors on exit
  if (!empty($error))
  {
    $tpl->assign('error', implode('<br/>', $error));
  }
  else
  {

    // Set account info
    if (updateUser($userId, $name, $email, null, $school, $acctType))
    {
      $_SESSION['msg']['success'] = 'Account type updated.';
      header('Location: editAccount.php');
      exit;
    }
    else
      $tpl->assign('error', 'Error updating account type.');
  }
}

// Get user information
if ($user = getUserInfoById($userId))
  $tpl->assign($user);

// Get available schools
if ($schools = getSchools())
  $tpl->assign("schools", $schools);

// Get security fields
$tpl->assign('get_rand', md5(session_id()));
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'COMTOR', 'href' => 'index.php');
$text = 'Edit Account';
if ($userId != $_SESSION['userId'])
{
  $breadcrumbs[] = array('text' => 'Account Management', 'href' => 'manageAccounts.php');
  $text = isset($user['name']) ? $user['name'] : 'User #'.$userId;
}
$breadcrumbs[] = array('text' => $text, 'href' => 'editAcct.php?userId='.$userId);
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("acct_edit.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
