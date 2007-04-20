<?
if(!isset($_POST['submit'])){	
	header("Location: http://csjava/~brigand2/index.php");
	exit;
}

require_once 'Text/Password.php';
require_once 'Mail.php';

//form data
$email = $_POST['email'];
		
//connect to database
mysql_connect('localhost', 'brigand2', 'joeBrig');
mysql_select_db('comtor');

$result = mysql_query("SELECT email FROM users WHERE email='$email'");
if (mysql_num_rows($result) == 0) {
	$message = "Username does not exist! <a href=\"recoverPasswordForm.php\">Go back</a>.";
}
else {
//create temp password
$tempPassword = Text_Password::create();
$cryptPassword = crypt($tempPassword, 'cm');

mysql_query("UPDATE users SET password='$cryptPassword' WHERE email='$email'");

//email password to user
$headers['From'] = 'CommentMentor@tcnj.edu';
$headers['To'] = $email;
$headers['Subject'] = 'Account Information';
$body = "Your Comment Mentor account can be accessed using:\n\nEmail: $email\nPassword: $tempPassword\n\nComment Mentor: http://csjava.tcnj.edu/~brigand2/";
$params['host'] = 'smtp.tcnj.edu';
$mail_object =& Mail::factory('smtp', $params);
$mail_object->send($email, $headers, $body);

$message = "Your password has been mailed to you! <a href=\"loginForm.php\">Login here</a>.";
}
?>


<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Comment Mentor</title>

</head>

<body>
  <center>

<hr noshade="noshade" width="65%"/>
<code> /* // # rem ' /** (* // */ // rem ' # # /**/ */ (* *) ' rem *) </code>
<hr noshade="noshade" width="65%"/>

    <table cellpadding="1" cellspacing="0" border="0" width="95%">
    <tr></tr>
    <tr>
    <td align="center">
    <table id="frame" cellpadding="3" cellspacing="3" border="0">
    <tr>
      <td>&nbsp;</td>
      <td>
        <table cellpadding="0" cellspacing="0">
         <tr>
           <td align="center">
            <img alt="ComTor" src="comtor.png" border="0">
           </td>
         </tr>
        </table>
      </td>
      <td>&nbsp;</td>
    </tr>
    </table>
    </td>
    </tr>
    </table>

<br/><br/>

  <table cellpadding="1" cellspacing="1" border="0">
   <tr>
     <td>[<a href="topten.jsp">Top 10 Comments</a>] </td>
	 <td>[<a href="moderate.jsp">Moderate Comments</a>] </td>
     <td>[<a href="features.html">Features We Measure</a>] </td>
     <td>[<a href="faq.html">FAQ</a>] </td>
     <td>[<a href="comtor.tar.gz">Download</a>] </td>
   </tr>
  </table>
  <table cellpadding="1" cellspacing="1" border="0">
   <tr>
   	 <td>[<a href="index.php">Home</a>] </td>
     <td>[<a href="signup.php">Create An Account</a>] </td>
	 <td>[<a href="recoverPasswordForm.php">Password Recovery</a>] </td>
   </tr>
  </table>

<br/><br/>

  <table>
   <tr>
     <td align="center"><? echo $message; ?></td>
   </tr>
  </table>
   
<br/><br/>   

<hr noshade="noshade" width="65%"/>
<font size="2">
<a href="about.html">About Comment Mentor</a> | &copy; 2006 TCNJ
</font>
<br/><br/>
<a href="http://www.tcnj.edu"><img src="tcnj_logo-small.gif" border="0"></a>
</center>
</body>
</html>