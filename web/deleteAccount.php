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

//if the user confirmed the deleting of an account
if($_GET['confirm'] == "yes")
{
  //connect to database
  include("connect.php");

  //delete the account
  mysql_query("DELETE FROM users WHERE userID='$userID'");
  mysql_query("DELETE FROM data WHERE userID='$userID'");
  $message = "The account has been deleted!";
  if($_SESSION['acctType'] != "admin"){session_destroy();}
}
//if the user hasn't confirmed yet, double check to make sure they want to delete the account
else {
  $message = "Are you sure you want to delete the account? <a href=\"deleteAccount.php?id=$userID&confirm=yes\">Yes</a> <a href=\"index.php\">No</a>";
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
