<?
if(!isset($_POST['submit'])){	
	header("Location: http://csjava/~brigand2/");
	exit;
}
require_once 'Text/Password.php';
require_once 'Mail.php';

//form data
$name = trim($_POST['name']);
$email = $_POST['email'];
$school = trim($_POST['school']);

//connect to database
mysql_connect('localhost', 'brigand2', 'joeBrig');
mysql_select_db('comtor');
  
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
//email temp password to user
$headers['From'] = 'CommentMentor@tcnj.edu';
$headers['To'] = $email;
$headers['Subject'] = 'Account Validation';
$body = "Dear $name,\n\nThank you for registering with Comment Mentor.  Your account has been successfully created.  Please use your email address and the following temporary password to login within 30 days.  At that time, you will have the option to change your password.\n\nComment Mentor: http://csjava.tcnj.edu/~brigand2/\n\nPassword: $tempPassword";
$params['host'] = 'smtp.tcnj.edu';
$mail_object =& Mail::factory('smtp', $params);
$mail_object->send($email, $headers, $body);

$message = "Congratulations $name!  Your account has been successfully created.<br>Please check your email for a temporary password to be used at your first login.<br>Please click <a href=\"index.php\">here</a> to login.";
}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"><!-- InstanceBegin template="/Templates/template.dwt.php" codeOutsideHTMLIsLocked="false" -->
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<!-- InstanceBeginEditable name="doctitle" -->
<title>Comment Mentor</title>
<!-- InstanceEndEditable -->
<!-- InstanceBeginEditable name="head" -->

<!-- InstanceEndEditable -->
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
		  <img alt="ComTor" src="img/comtor.png" border="0">
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

<br><br>
	
<table cellpadding="1" cellspacing="1" border="0">
 <tr>
  <td>[<a href="topten.jsp">Top 10 Comments</a>] </td>
  <td>[<a href="moderate.jsp">Moderate Comments</a>] </td>
  <td>[<a href="features.php">Features We Measure</a>] </td>
  <td>[<a href="faq.php">FAQ</a>] </td>
  <td>[<a href="comtor.tar.gz">Download</a>] </td>
 </tr>
</table>
<table cellpadding="1" cellspacing="1" border="0">
 <tr>
  <td>[<a href="index.php">Home</a>] </td>
	<?	
  	if(isset($_SESSION['userID']) && ($_SESSION['userID'] != ""))
	{
	  ?><td>[<a href="changePasswordForm.php">Change Password</a>] </td>
		<td>[<a href="reports.php">View Report</a>] </td>
	 	<td>[<a href="disableAccount.php">Disable Account</a>] </td>
	 	<td>[<a href="logout.php">Logout</a>] </td><?
	}
	else
	{
		?><td>[<a href="registerForm.php">Create An Account</a>] </td>
  		<td>[<a href="recoverPasswordForm.php">Password Recovery]</a> </td><?
	}
	?>
 </tr>
</table><?

if($_SESSION['acctType']=="admin")
{?>
<table cellpadding="1" cellspacing="1" border="0">
 <tr>
  <td>[<a href="manageAccounts.php">Account Management</a>] </td>
  <td>[<a href="adminReports.php">Admin Reports</a>] </td>
 </tr>
</table><?
}?> 

<br><br>
	
<!-- InstanceBeginEditable name="EditRegion" -->

<table>
 <tr>
  <td align="center"><? echo $message; ?></td>
 </tr>
</table>

<!-- InstanceEndEditable -->

<br><br>
<hr noshade="noshade" width="65%"/>
<font size="2">
<a href="about.php">About Comment Mentor</a> | &copy; 2006 TCNJ
</font>
<br/><br/>
<a href="http://www.tcnj.edu"><img src="img/tcnj_logo-small.gif" border="0"></a>
</center>
</body>
<!-- InstanceEnd --></html>