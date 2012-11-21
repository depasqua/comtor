<?php

require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Get session id
// Session already started in loginCheck.php
$userId = $_SESSION['userId'];

if (isset($_POST['oldPassword']) && isset($_POST['newPassword']) && isset($_POST['confirmPassword']))
{
  // Form data
  $oldPassword = stripslashes($_POST['oldPassword']);
  $newPassword = trim($_POST['newPassword']);
  $confirmPassword = trim($_POST['confirmPassword']);

  // Connect to database
  include("connect.php");

  // Check to see if the password from the user matches the password in the database
  if (correctPassword($userId, $oldPassword))
  {
    // Check to see if the 2 new passwords match
    if($newPassword == $confirmPassword)
    {
      $newPassword = crypt($newPassword, 'cm');
      // Update new password
      if (setPassword($userId, $newPassword))
        $tpl->assign('success', "Password changed successfully!");
      else
        $tpl->assign('error', "Unknown error changing password!");
    }
    else
      $tpl->assign('error', "Passwords don't match!");
  }
  else
    $tpl->assign('error', "Incorrect password!");
}

// Fetch template
$tpldata = $tpl->fetch("change_pass.tpl");
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");
?>
