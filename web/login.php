<?
session_start();
if(!isset($_POST['submit']) || isset($_SESSION['userId']))
{
  include("redirect.php");
}

//form data
$email = $_POST['email'];
$password = stripslashes($_POST['password']);

// Add @tcnj.edu if there is no @
if (strpos($email, "@") === false)
  $email .= "@tcnj.edu";

//connect to database
include("connect.php");

// Validate email and password
if (!emailExists($email))
{
  $message = "Username does not exist!";
}
else
{
  $row = getUserInfoByEmail($email);
  $userId = $row['userId'];
  $acctStatus = $row['acctStatus'];

  //check for account status
  if($acctStatus == "enabled")
  {
    //crypt password and check result against user's current password
    $cryptPassword = crypt($password, 'cm');
    if($cryptPassword == $row['password'])
    {
      //if first login, set validated date and time
      if($row['validatedDT'] == NULL)
      {
        setValidationDate($userId);
      }
      // Set last login
      else
      {
        setLastLoginDate($userId);
      }

      // set Id, account type
      $_SESSION['userId'] = $userId;
      $_SESSION['acctType'] = $row['acctType'];
      $_SESSION['username'] = $row['name'];
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


 <? echo $message; ?><br /><a href="loginForm.php">Back to login</a>.

<?php include_once("footer.php"); ?>
