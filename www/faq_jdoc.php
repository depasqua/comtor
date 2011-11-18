<?php

session_start();

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'FAQ', 'href' => 'faq.php');
$breadcrumbs[] = array('text' => 'Javadoc', 'href' => 'faq_jdoc.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("faq_jdoc.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
