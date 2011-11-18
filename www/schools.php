<?php
$acctTypes = "admin";
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Connect to database
include("connect.php");

// Random string added to important links for security
$md5Rand = md5(session_id());
$tpl->assign('rand', $md5Rand);

// Get parameters
$state = isset($_GET['state']) ? $_GET['state'] : "";
$tpl->assign("state", $state);
$schoolId = isset($_GET['schId']) ? $_GET['schId'] : false;

// Check that the school exists
if ($schoolId && !schoolExists($schoolId))
  $tpl->assign('error', "School id invalid.");
  
// Get school information
if ($state == "edit")
{
  // Check if info was posted
  if (!empty($_POST))
  {
    $tpl->assign('school', @$_POST['school']);
    if (!isset($_POST['school']) || empty($_POST['school']))
      $tpl->assign('error', "School name invalid.");
    else if (schoolNameExists($_POST['school'], $schoolId))
      $tpl->assign('error', "School name already exists.");
    else
    {
      $rtn = editSchool($schoolId, $_POST['school']);
      
      // Check return value
      switch ($rtn)
      {
        case 0:
          $tpl->assign('success', "School successfully updated.");
          break;
        case 1:
          $tpl->assign('error', "School id invalid.");
          break;
        case 2:
          $tpl->assign('error', "School name already exists.");
          break;
        default:
          $tpl->assign('error', "Error updating school.");
          break;            
      }
    }    
  }
  if ($school = getSchool($schoolId))
    $tpl->assign('school', $school);
  else
    $tpl->assign('error', "School id invalid.");
}
// Add new school
else if ($state == "new")
{
  // Check if info was posted
  if (!empty($_POST))
  {
    $tpl->assign('school', @$_POST['school']);
    if (!isset($_POST['school']) || empty($_POST['school']))
      $tpl->assign('error', "School name invalid.");
    else if (schoolNameExists($_POST['school'], $schoolId))
    {
      $tpl->assign('error', "School name already exists.");
    }
    else
    {
      $rtn = addSchool($_POST['school']);
      
      // Check return value
      switch ($rtn)
      {
        case 0:
          $tpl->assign('success', "School successfully updated.");
          break;
        case 1:
          $tpl->assign('error', "School name already exists.");
          break;
        default:
          $tpl->assign('error', "Error updating school.");
          break;            
      }
    }    
  }
}

// List schools
if ($schools = getSchools())
  $tpl->assign('schools', $schools);

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Schools', 'href' => 'schools.php');
switch ($state)
{
  case "new":
    $breadcrumbs[] = array('text' => 'Add New School', 'href' => 'schools.php?state=new');
    break;
  case "edit":
    $breadcrumbs[] = array('text' => 'Edit School', 'href' => 'schools.php?&schId={$schoolId}amp;state=edit');
    break;
}
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Fetch template
$template = "schools_all.tpl";
if ($state == "new" || $state == "edit")
  $template = "schools_edit.tpl";
$tpldata = $tpl->fetch($template);
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
