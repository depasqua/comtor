<?
session_start();
//check for admin permissions
if(!isset($_SESSION['userID']) || ($_SESSION['acctType'] != "admin")) {
  include("redirect.php");
}

//set user id
$userID = $_GET['id'];

//if the action was confirmed, set account to admin
if($_GET['confirm'] == "yes")
{
  //connect to database
  include("connect.php");

  //set account to admin
  mysql_query("UPDATE users SET acctType='admin' WHERE userID='$userID'");
  $message = "The account is an administrator!";
}
//if the action was NOT confirmed, set account to user
else if($_GET['confirm'] == "no")
{
  //connect to database
  include("connect.php");

  //set account to user
  mysql_query("UPDATE users SET acctType='user' WHERE userID='$userID'");
  $message = "The account is NOT an administrator!";
}
//if no confirmation was selected, prompt user to select yes or no
else {
  $message = "Do you want to make this account an administrator? <a href=\"changeAcctType.php?id=$userID&confirm=yes\">Yes</a> <a href=\"changeAcctType.php?id=$userID&confirm=no\">No</a> <a href=\"manageAccounts.php\">Cancel</a>";
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
