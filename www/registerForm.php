<?php

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Get available schools
if ($schools = getSchools())
  $tpl->assign("schools", $schools);

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Register', 'href' => 'register.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("register.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
