<?
if(!isset($_POST['submit'])){
  include("redirect.php");
}

//form data
$email = $_POST['email'];
$password = stripslashes($_POST['password']);

//connect to database
include("connect.php");

//validate email and password
$result = mysql_query("SELECT * FROM users WHERE email='$email'");
if (mysql_num_rows($result) == 0) {
  $message = "Username does not exist!";
}
else {
  $row = mysql_fetch_array($result);
  $userID = $row['userID'];
  $acctStatus = $row['acctStatus'];

  //check for account status
  if($acctStatus == enabled)
  {
    //crypt password and check result against user's current password
    $cryptPassword = crypt($password, 'cm');
    if($cryptPassword == $row['password'])
    {
      //if first login, set validated date and time
      if($row['validatedDT'] == NULL){
        mysql_query("UPDATE users SET validatedDT=NOW(), lastLogin=NOW() WHERE userID='$userID'");
      }
      //set last login
      else{
        mysql_query("UPDATE users SET lastLogin=NOW() WHERE userID='$userID'");
      }

      session_start();
      //start session and set ID, account type
      $_SESSION['userID'] = $userID;
      $_SESSION['acctType'] = $row['acctType'];
      include("redirect.php");
    }
    else{
      $message = "Incorrect password!";
    }
  }
  else{
    $message = "Username does not exist!";
  }
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center">
   <? echo $message; ?><br><a href="loginForm.php">Back to login</a>.
  </td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
