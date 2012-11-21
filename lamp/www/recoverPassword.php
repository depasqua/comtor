<?php
if(!isset($_POST['submit']))
{
  include("redirect.php");
}

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");
require_once("config.php");

require_once 'Text/Password.php';
require_once 'Mail.php';

//form data
$email = $_POST['email'];

//connect to database
include("connect.php");

//checks to see if email is in the database
if (!emailExists($email))
{
  $message = "Username does not exist! <a href=\"recoverPasswordForm.php\">Go back</a>.";
}
else
{
  // Create temp password
  $tempPassword = Text_Password::create();
  $cryptPassword = crypt($tempPassword, 'cm');

  // Update password
  if (!setPasswordByEmail($email, $cryptPassword))
  {
    $message = "Error creating a temporary password.<br/>" . mysql_error();
  }
  else
  {
    require_once('generalFunctions.php');

    // E-mail new password to user
    $subject = 'Account Information';
    $body = "Your Comment Mentor account can be accessed using:\n\nEmail: $email\nPassword: $tempPassword\n\nComment Mentor: http://" . URL_PATH;
    $body = nl2br($body);
    $result = sendMail($body, $email, null, $subject);

    $message = "Your password has been mailed to you! <a href=\"loginForm.php\">Login here</a>.";
  }
}

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

$tpl->assign('tpldata', $message);

// Display template
$tpl->display("htmlmain.tpl");

?>
