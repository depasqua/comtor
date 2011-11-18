<?php
$acctTypes = "admin";
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

//connect to database
include("connect.php");

require_once("generalFunctions.php");

// Get database stats
$tpl->assign('stats', getDbStats());

// Get number of current users
$tpl->assign('currentUsers', numCurrentUsers());

// Get use of each doclet
$tpl->assign('doclets', getDocletUsage());

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Admin Reports', 'href' => 'adminReports.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("admin_reports.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
