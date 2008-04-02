<?
if(!isset($_POST['submit']))
{
  include("redirect.php");
}

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
    // E-mail new password to user
    $headers['From'] = 'CommentMentor@tcnj.edu';
    $headers['To'] = $email;
    $headers['Subject'] = 'Account Information';
    $body = "Your Comment Mentor account can be accessed using:\n\nEmail: $email\nPassword: $tempPassword\n\nComment Mentor: http://csjava.tcnj.edu/~sigwart4/";
    $params['host'] = 'smtp.tcnj.edu';
    $mail_object =& Mail::factory('smtp', $params);
    $mail_object->send($email, $headers, $body);

    $message = "Your password has been mailed to you! <a href=\"loginForm.php\">Login here</a>.";
  }
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
