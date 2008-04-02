<?php require_once("loginCheck.php"); ?>
<?
if(!isset($_POST['submit'])){
  include("redirect.php");
}

//get session id
// Session already started in loginCheck.php
$userId = $_SESSION['userID'];

//form data
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
      $message = "Password changed successfully!<br/><a href=\"index.php\">Go back to the homepage</a>.";
    else
      $message = "Unknown error changing password!";
  }
  else
  {
    $message = "Passwords don't match!<br/><a href=\"changePasswordForm.php\">Go back to previous page</a>.";
  }
}
else
{
  $message = "Incorrect password!<br/><a href=\"changePasswordForm.php\">Go back to previous page</a>.";
}
?>
<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
