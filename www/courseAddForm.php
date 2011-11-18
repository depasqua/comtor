<?php
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

include ("connect.php");
if ($_SESSION['acctType'] == "admin")
{
  // Get all professors
  if (($profs = getUsers(array("userId", "name"), "enabled", "professor")) !== false)
    $tpl->assign('profs', $profs);
}

// Display options for year (1 year prior, 3 years later)
$year = (int)(date("Y"));
$tpl->assign('start_year', $year-1);
$tpl->assign('end_year', $year+3);

// Get security fields
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Add Course', 'href' => 'courseAddForm.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
//$tooltips = file_get_contents('tooltips/test.html');
//$tpl->assign('tooltips', $tooltips);

// Fetch template
$tpldata = $tpl->fetch("course_add.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
