<?php $acctTypes = "admin"; ?>
<?php require_once("loginCheck.php"); ?>
<?php
// Check for user id
if (isset($_GET['userId']))
{
  $userId = $_GET['userId'];
}
else
{
  $_SESSION['msg']['error'] = "No account specified for deletion!";
  header("Location: manageAccounts.php");
  exit;
}

// Check if rand is set and correct
if(isset($_GET['rand']) && $_GET['rand'] == md5(session_id()))
{
  // Connect to database
  include("connect.php");

  // Delete the account and check for success
  if (deleteUser($userId))
  {
    $_SESSION['msg']['success'] = "The account has been deleted!";
  }
  else
  {
    $_SESSION['msg']['error'] = "Error deleting the specified account!";
  }

}
else
{
  $_SESSION['msg']['error'] = "Security Error.";
}

    header("Location: manageAccounts.php");
    exit;
?>
