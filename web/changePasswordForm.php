<?
session_start();
if(!isset($_SESSION['userID'])) {
	//redirect to comment mentor
	header("Location: http://csjava/~brigand2/loginForm.php");
	exit;
}
?>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Comment Mentor</title>
<script type="text/javascript">
function verify() {
var themessage = "You are required to complete the following fields: ";
if (document.form.oldPassword.value=="") {
themessage = themessage + " - Old Password";
}
if (document.form.newPassword.value=="") {
themessage = themessage + " -  New Password";
}
if (document.form.confirmPassword.value=="") {
themessage = themessage + " -  Confirm Password";
}
//alert if fields are empty and cancel form submit
if (themessage == "You are required to complete the following fields: ") {
	if((document.form.newPassword.value.length<6) || (document.form.confirmPassword.value.length<6)) {
		themessage = "The new password must be a minimum of 6 characters!";
	}
	if(themessage == "You are required to complete the following fields: "){}
	else {
		alert(themessage);
		return false;
	}
}
else {
alert(themessage);
return false;
}
}
</script>
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
	 <td>[<a href="changePasswordForm.php">Change Password</a>] </td>
	 <td>[<a href="report.php">View Report</a>] </td>
	 <td>[<a href="deleteAccount.php">Delete Account</a>] </td>
	 <td>[<a href="logout.php">Logout</a>] </td>
   </tr>
  </table>

<br/><br/>

   <form action="changePassword.php" method="post" name="form">
	<table id="frame">
	 <tr>
	  <td>Old Password:</td>
	 <tr>
	  <td><input type="password" name="oldPassword"></td>
	 <tr>
	  <td>New Password:</td>
	 <tr>
	  <td><input type="password" name="newPassword"></td>
	 <tr>
	  <td>Confirm New Password:</td>
	 <tr>
	  <td><input type="password" name="confirmPassword"></td> 
	 <tr>
      <td><input type="submit" name="submit" value="Submit" onClick="return verify();"></td>
    </table>
   </form>
   
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