<?php
$acctTypes = "admin";
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

//Define the default and maximum number of users to be shown
define("DEFAULT_LIST_TOTAL", 25);
define("MAX_LIST_TOTAL", 100);

// Connect to database
include("connect.php");

// Random string added to important links for security
$md5Rand = md5(session_id());
$tpl->assign('rand', $md5Rand);

// Check if there is a limit set for enabled accounts
if (!isset($_GET['lowerEn']) || !is_numeric($_GET['lowerEn']))
  $_GET['lowerEn'] = 0;
if (!isset($_GET['totalEn']) || !is_numeric($_GET['totalEn']))
  $_GET['totalEn'] = DEFAULT_LIST_TOTAL;
// Check that total is less than the maximum valid
else if ($_GET['totalEn'] > MAX_LIST_TOTAL)
  $_GET['totalEn'] = MAX_LIST_TOTAL;

// Check if there is a limit set for disabled accounts
if (!isset($_GET['lowerDis']) || !is_numeric($_GET['lowerDis']))
  $_GET['lowerDis'] = 0;
if (!isset($_GET['totalDis']) || !is_numeric($_GET['totalDis']))
  $_GET['totalDis'] = DEFAULT_LIST_TOTAL;
// Check that total is less than the maximum valid
else if ($_GET['totalDis'] > MAX_LIST_TOTAL)
  $_GET['totalDis'] = MAX_LIST_TOTAL;

// List accounts that are currently enabled
if ($users = getUsers(array("userId", "name", "school", "acctType"), "enabled", "all", $_GET['lowerEn'], $_GET['totalEn']))
  $tpl->assign('usersE', $users);

require_once("generalFunctions.php");

// Add links to view other courses if there are any
$numUsers = getNumUsers("enabled");
$tpl->assign('usersELinks', listPages($_GET['lowerEn'], $_GET['totalEn'], $numUsers, "lowerEn", "totalEn", "lowerDis={$_GET['lowerDis']}&amp;totalDis={$_GET['totalDis']}"));

// List accounts that are currently disabled
if ($users = getUsers(array("userId", "name", "school", "acctType"), "disabled", "all", $_GET['lowerDis'], $_GET['totalDis']))
  $tpl->assign('usersD', $users);

// Add links to view other courses if there are any
$numUsers = getNumUsers("disabled");
$tpl->assign('usersDLinks', listPages($_GET['lowerDis'], $_GET['totalDis'], $numUsers, "lowerDis", "totalDis", "lowerEn={$_GET['lowerEn']}&amp;totalEn={$_GET['totalEn']}"));

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'COMTOR', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Account Management', 'href' => 'manageAccounts.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("acct_manage_all.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
