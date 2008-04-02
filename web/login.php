<?
if(!isset($_POST['submit'])){
  include("redirect.php");
}

//form data
$email = $_POST['email'];
$password = stripslashes($_POST['password']);

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
  $userID = $row['userID'];
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
        setValidationDate($userID);
      }
      // Set last login
      else
      {
        setLastLoginDate($userID);
      }

      session_start();
      //start session and set ID, account type
      $_SESSION['userID'] = $userID;
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
