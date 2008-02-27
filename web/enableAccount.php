<?php require_once("loginCheck.php"); ?>
<?php

//set user id
$userID = $_GET['id'];

//if the user confirmed the enabling of an account
if($_GET['confirm'] == "yes")
{
  //connect to database
  include("connect.php");

  //enable the account
  mysql_query("UPDATE users SET acctStatus='enabled' WHERE userID='$userID'");
  $message = "The account has been enabled!";
  if($_SESSION['acctType'] != "admin"){session_destroy();}
}
//if the user hasn't confirmed yet, double check to make sure they want to enable the account
else {
  $message = "Are you sure you want to enable the account? <a href=\"enableAccount.php?id=$userID&confirm=yes\">Yes</a> <a href=\"index.php\">No</a>";
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
