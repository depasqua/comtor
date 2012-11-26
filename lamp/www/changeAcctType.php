<?php
$acctTypes = "admin";
require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

//set user id
$userId = $_GET['userId'];

//if the action was confirmed, set account to admin
if($_GET['confirm'] == "yes")
{
  //connect to database
  include("connect.php");

  //set account to admin
  if (setUserAcctType($userId, "admin"))
    $message = "The account is an administrator!";
  else
    $message = "Error!";
}
//if the action was NOT confirmed, set account to user
else if($_GET['confirm'] == "no")
{
  //connect to database
  include("connect.php");

  //set account to user
  if (setUserAcctType($userId, "user"))
    $message = "The account is NOT an administrator!";
  else
    $message = "Error!";
}
//if no confirmation was selected, prompt user to select yes or no
else {
  $message = "Do you want to make this account an administrator? <a href=\"changeAcctType.php?userId=$userId&confirm=yes\">Yes</a> <a href=\"changeAcctType.php?userId=$userId&confirm=no\">No</a> <a href=\"manageAccounts.php\">Cancel</a>";
}

$tpl->assign('tpldata', $message);

// Display template
$tpl->display("htmlmain.tpl");

?>
