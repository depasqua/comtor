<?php
require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Video Tutorials', 'href' => 'tutorials.php');
$breadcrumbs[] = array('text' => 'Tutorial', 'href' => 'tutorial.php?video='.$_GET['video']);
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign video
$tpl->assign("video", "tutorials/".$_GET['video']);

// Fetch template
$tpldata = $tpl->fetch("tutorial.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");
?>
