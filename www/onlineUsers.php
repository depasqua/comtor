<?php

$acctTypes = "admin";
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

//connect to database
include("connect.php");

// Get number of current users
$tpl->assign('users', getCurrentUsers());


// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Online Users', 'href' => '#');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("onlineUsers.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
