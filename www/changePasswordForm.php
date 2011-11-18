<?php

require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("change_pass.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
