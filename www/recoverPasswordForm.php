<?php

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Password Recovery', 'href' => 'recoverPasswordForm.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
$tooltips = file_get_contents('tooltips/recoverPassword.html');
$tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("recover_password.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
