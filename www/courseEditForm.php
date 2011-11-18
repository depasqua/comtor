<?php
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

include("connect.php");

/* Get information on the course to be edited and check permissions*/
if (isset($_GET['courseId']))
{
  // Get course info which also checks that the course id is valid
  $courseInfo = getCourseInfo($_GET['courseId'], true);
  if ($courseInfo === false)
  {
    $_SESSION['msg']['error'] = "Invalid course specified.";
    header("Location: courses.php");
    exit;
  }

  // Check that this is this professors course if user is professor
  if ($_SESSION['acctType'] == "professor" && $_SESSION['userId'] != $courseInfo['profId'])
  {
    $_SESSION['msg']['error'] = "You cannot disable any course other than your own.";
    header("Location: courses.php");
    exit;
  }
}
// Indicate error and redirect
else
{
  $_SESSION['msg']['error'] = "Course to be edited was not specified.";
  header("Location: courses.php");
  exit;
}

if ($_SESSION['acctType'] == "admin")
{
  // Get all professors
  if (($profs = getUsers(array("userId", "name"), "enabled", "professor")) !== false)
    $tpl->assign('profs', $profs);
}

// Split semester of course into season and year
$arr = explode(" ", $courseInfo['semester']);
$courseInfo['season'] = $arr[0];
$courseInfo['year'] = $arr[1];

// Display options for year (1 year prior, 3 years later)
$year = (int)(date("Y"));
if ($courseInfo['year'] >= $year)
  $tpl->assign('start_year', $year-1);
else
  $tpl->assign('start_year', $courseInfo['year']-1);
if ($courseInfo['year'] < $year+3)
  $tpl->assign('end_year', $year+3);
else
  $tpl->assign('end_year', $courseInfo['year']+3);

$tpl->assign($courseInfo);

// Get security fields
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Tell template that we are editing a course
$tpl->assign('edit', true);

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Edit Course', 'href' => 'courseEditForm.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch("course_add.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
