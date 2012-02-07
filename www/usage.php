<?php
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Connect to database
require_once("connect.php");

// Check for admin permissions, else use current user id
if ((isset($_GET['userId'])) && is_numeric($_GET['userId']) && ($_SESSION['acctType'] == "admin"))
  $userId = $_GET['userId'];
else
  $userId = $_SESSION['userId'];

/* Display current user information */
if (($userInfo = getUserInfoById($userId)) !== false)
  $tpl->assign($userInfo);
else
{
  // Redirect with error message
  $_SESSION['msg']['error'] = "Specified user does not exist!";
  require_once("redirect.php");
}

// Assign date format to template
$dateFormat = "l F j, Y g:i:s A";
$tpl->assign('dateFormat', $dateFormat);

require_once("generalFunctions.php");

// Display use of each doclet
if (getUserAcctType($userId) == "student")
  $tpl->assign('doclets', getDocletUsage($userId));

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'System Usage', 'href' => 'usage.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
$tooltips = file_get_contents('tooltips/usage.html');
$tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("usage.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");
?>
