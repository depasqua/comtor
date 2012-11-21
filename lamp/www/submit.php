<?php

$acctTypes = "student";
require_once("loginCheck.php");
require_once("config.php");

// Check that there is a course set
if (!isset($_SESSION['courseId']))
{
  $_SESSION['msg']['error'] = "You must select the course that you wish to submit files for.";
  header("Location: index.php");
  exit;
}

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Connect to database
include_once("connect.php");

// Get list of assignments if there is a course set in the session
$params = array('time'=>time());
if ($assignments = getAssignments($_SESSION['courseId'], $params))
  $tpl->assign('assignments', $assignments);
else
{
  $_SESSION['msg']['error'] = "There are no assignments open.";
  header("Location: dropbox.php");
  exit;
}

// Get report types (doclets) from database
if (($doclets = getDoclets()) !== false)
  $tpl->assign('doclets', $doclets);

// Close MySQL database
mysql_close();

// Fetch template
$tpldata = $tpl->fetch('submit_files.tpl');

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Submit', 'href' => 'submit.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
$tooltips = file_get_contents('tooltips/submit.html');
$tpl->assign('tooltips', $tooltips);

// Assign page html
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
