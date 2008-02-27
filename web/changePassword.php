<?php require_once("loginCheck.php"); ?>
<?
if(!isset($_POST['submit'])){
  include("redirect.php");
}

//get session id
// Session already started in loginCheck.php
$userID = $_SESSION['userID'];

//form data
$oldPassword = stripslashes($_POST['oldPassword']);
$newPassword = trim($_POST['newPassword']);
$confirmPassword = trim($_POST['confirmPassword']);

//connect to database
include("connect.php");

//select current password
$result = mysql_query("SELECT password FROM users WHERE userID='$userID'");
$row = mysql_fetch_array($result);
$currentPassword = $row['password'];

$cryptPassword = crypt($oldPassword, 'cm');
//check to see if the password from the user matches the password in the database
if($currentPassword == $cryptPassword) {
  //check to see if the 2 new passwords match
  if($newPassword == $confirmPassword) {
    $newPassword = crypt($newPassword, 'cm');
    //update new password
    mysql_query("UPDATE users SET password='$newPassword', passwordChangeDT=NOW() WHERE userID='$userID'");
    $message = "Password changed successfully!<br><a href=\"index.php\">Go back to the homepage</a>.";
  }
  else {
    $message = "Passwords don't match!<br><a href=\"changePasswordForm.php\">Go back to previous page</a>.";
  }
}
else {
  $message = "Incorrect password!<br><a href=\"changePasswordForm.php\">Go back to previous page</a>.";
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
