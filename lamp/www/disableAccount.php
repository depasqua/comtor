<?php require_once("loginCheck.php"); ?>
<?php

// Check for admin permissions, otherwise use current user id
if (isset($_GET['userId']))
{
  // Use the id if the current user is admin
  if ($_SESSION['acctType'] == "admin")
  {
    $userId = $_GET['userId'];
  }
  // If current user is not admin, use the id only if it is current user
  else if ($_SESSION['userId'] == $_GET['userId'])
  {
    $userId = $_SESSION['userId'];
  }
  else
  {
    $_SESSION['msg']['error'] = "You do not have permissions to disable another user!";
    header("Location: index.php");
    exit;
  }
}
else
{
  if ($_SESSION['acctType'] == "admin")
  {
    $_SESSION['msg']['error'] = "No account specified to be disabled!";
    header("Location: manageAccounts.php");
    exit;
  }
  else
  {
    // Redirect
    $_SESSION['msg']['error'] = "No account specified to be disabled!";
    require_once("redirect.php");
  }
}

// Check if rand is set and correct
if(isset($_GET['rand']) && $_GET['rand'] == md5(session_id()))
{
  // Connect to database
  include("connect.php");

  // Disable the account
  if (disableUser($userId))
  {
    $_SESSION['msg']['success'] = "The account has been disabled!";
    if($_SESSION['acctType'] != "admin")
    {
      session_destroy();
      session_start();
    }
  }
  else
  {
    $_SESSION['msg']['error'] = "There was an error disabling the account.";
  }
}
else
{
  $_SESSION['msg']['error'] = "Security Error.";
}

// Redirect
if ($_SESSION['acctType'] == "admin")
  header("Location: manageAccounts.php");
else
  header("Location: index.php");
exit();
?>
