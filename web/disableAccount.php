<?php require_once("loginCheck.php"); ?>
<?php

//check for admin permissions, otherwise use current user id
if((isset($_GET['id'])) && ($_SESSION['acctType'] == "admin"))
{
  $userID = $_GET['id'];
}
else {
  $userID = $_SESSION['userID'];
}

//if the user confirmed the disabling of an account
if($_GET['confirm'] == "yes")
{
  //connect to database
  include("connect.php");

  //disable the account
  mysql_query("UPDATE users SET acctStatus='disabled' WHERE userID='$userID'");
  $message = "The account has been disabled!";
  if($_SESSION['acctType'] != "admin"){session_destroy();}
}
//if the user hasn't confirmed yet, double check to make sure they want to disable the account
else {
  $message = "Are you sure you want to disable the account? <a href=\"disableAccount.php?id=$userID&confirm=yes\">Yes</a> <a href=\"index.php\">No</a>";
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
