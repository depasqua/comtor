<?php $acctTypes = "admin"; ?>
<?php require_once("loginCheck.php"); ?>
<?php

// Redirect
function redirect()
{
  if ($_SESSION['acctType'] == "admin")
    header("Location: manageAccounts.php");
  else
    header("Location: index.php");
  exit();
}

// Set user id
if (isset($_GET['userId']))
  $userId = $_GET['userId'];
else
{
  $_SESSION['msg']['error'] = "No user specified to be enabled.";
  redirect();
}

// Check if rand is set and correct
if(isset($_GET['rand']) && $_GET['rand'] == md5(session_id()))
{
  //connect to database
  include("connect.php");

  // Enable the account
  if (enableUser($userId))
  {
    $_SESSION['msg']['success'] = "The account has been enabled!";
  }
  else
    $_SESSION['msg']['error'] = "There was an error disabling the account.";
}
else
{
  $_SESSION['msg']['error'] = "Security Error.";
}

redirect();
?>
