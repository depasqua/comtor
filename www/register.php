<?php
if(!isset($_POST['submit'])){
  include("redirect.php");
}

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");
require_once("config.php");

require_once 'Text/Password.php';
require_once 'Mail.php';

//form data
$name = trim($_POST['name']);
$email = $_POST['email'];

// School
if (!isset($_POST['school']))
  $error[] = 'School field not sent.';
else
{
  $school = $_POST['school'];
  if (!schoolExists($school))
  {
    $_SESSION['msg']['error'] = 'School is required.';
    header('Location: registerForm.php');
    exit;
  }
}

// Check that professor request link is filled in
if (isset($_POST['prof_req']))
  if (!isset($_POST['prof_link']) || empty($_POST['prof_link']))
  {
    $_SESSION['msg']['error'] = 'You must provide a link if you are requesting professor status.';
    header('Location: registerForm.php');
    exit;
  }

//connect to database
include("connect.php");

//check if email already exists
if (emailExists($email))
{
  $message = "An account is already registered with this email address! <a href=\"registerForm.php\">Go back</a>.";
}
else
{
  // Create temp password
  $tempPassword = Text_Password::create();
  $cryptPassword = crypt($tempPassword, 'cm');

  // Insert new user into database
  if ($userId = createNewUser($name, $email, $cryptPassword, $school))
  {
    // Make request to be professor if needed
    if (isset($_POST['prof_req']))
      if (requestAcctChange($userId, 'professor', $_POST['prof_link']) === false)
        $_SESSION['msg']['error'] = 'There was an error requesting professor status.  Please E-mail an adminstrator to make your request.';

    require_once('generalFunctions.php');

    $name = stripslashes($name);
    // E-mail temporary password to user
    $subject = 'Account Validation';
    $body = "<img src=\"https://secure.comtor.org/comtorapp/img/logo.gif\" alt=\"COMTOR\"/>\nDear $name,\n\nThank you for registering with COMTOR.  Your account has been successfully created.  Please use your email address and the following temporary password to login within 30 days.  At that time, you will have the option to change your password.\n\nCOMTOR: http://" . URL_PATH . "\n\nPassword = $tempPassword";
    $body = nl2br($body);
    $result = sendMail($body, $email, null, $subject);

    $message = "Congratulations $name!  Your account has been successfully created.<br/>Please check your email for a temporary password to be used at your first login.<br/>Please click <a href=\"index.php\">here</a> to login.";
  }
  else
  {
    $message = "Error creating user.";
  }
}

$tpl->assign('tpldata', $message);

// Display template
$tpl->display("htmlmain.tpl");

?>
