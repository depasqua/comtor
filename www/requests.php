<?php

require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Connect to database
require_once("connect.php");

// Random string added to important links for security
$md5Rand = md5(session_id());
$tpl->assign('rand', $md5Rand);

// Get user id to get requests for and the status of requests to fetch
$status = null;
$userId = null;
if ($_SESSION['acctType'] != "admin")
  $userId = $_SESSION['userId'];
else
  $status = 'pending';

// Get account type change requests
$req_acct_type = getAcctTypeRequests($status, $userId);
if ($req_acct_type !== false)
  $tpl->assign('req_acct_type', $req_acct_type);

// Get account removal requests
$req_acct_removal = getAcctRemovalRequests($status, $userId);
if ($req_acct_removal !== false)
  $tpl->assign('req_acct_removal', $req_acct_removal);

// Set possible requests
if ($userId != null)
{
  $tpl->assign('userId', $userId);

  // Change account type
  $acctTypes = getUserAcctTypes();
  $tpl->assign('acctTypes', $acctTypes);
  $tpl->assign('req_acctType', true);

  $tpl->assign('req_acctDelete', true);
}

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'COMTOR', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Requests', 'href' => 'requests.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("requests.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
