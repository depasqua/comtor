<?
if(!isset($_POST['submit'])){
  include("redirect.php");
}
require_once 'Text/Password.php';
require_once 'Mail.php';

//form data
$name = trim($_POST['name']);
$email = $_POST['email'];
$school = trim($_POST['school']);

//connect to database
include("connect.php");

//check if email already exists
$result = mysql_query("SELECT * FROM users WHERE email='$email'");
if (mysql_num_rows($result) > 0) {
  $message = "An account is already registered with this email address! <a href=\"signup.php\">Go back</a>.";
}
else {
//create temp password
$tempPassword = Text_Password::create();
$cryptPassword = crypt($tempPassword, 'cm');

//insert new user into database
mysql_query("INSERT INTO users(name, email, password, school, acctType, acctStatus) VALUES ('$name', '$email', '$cryptPassword', '$school', 'user', 'enabled')");

$name = stripslashes($name);
//email temporary password to user
$headers['From'] = 'CommentMentor@tcnj.edu';
$headers['To'] = $email;
$headers['Subject'] = 'Account Validation';
$body = "Dear $name,\n\nThank you for registering with Comment Mentor.  Your account has been successfully created.  Please use your email address and the following temporary password to login within 30 days.  At that time, you will have the option to change your password.\n\nComment Mentor: http://csjava.tcnj.edu/~sigwart4/\n\nPassword: $tempPassword";
$params['host'] = 'smtp.tcnj.edu';
$mail_object =& Mail::factory('smtp', $params);
$mail_object->send($email, $headers, $body);

$message = "Congratulations $name!  Your account has been successfully created.<br>Please check your email for a temporary password to be used at your first login.<br>Please click <a href=\"index.php\">here</a> to login.";
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
