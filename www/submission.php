<?php

session_start();

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Load and assign content
if (isset($_SESSION['content']))
{
  $tpl->assign('content', $_SESSION['content']);
  unset($_SESSION['content']);
}

// Load and assign breadcrumbs
if (isset($_SESSION['breadcrumbs']))
{
  $tpl->assign('breadcrumbs', $_SESSION['breadcrumbs']);
  unset($_SESSION['breadcrumbs']);
}
else
{
  $breadcrumbs = array();
  $breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
  $tpl->assign('breadcrumbs', $breadcrumbs);
}

// Fetch template
$tpldata = $tpl->fetch("general.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
