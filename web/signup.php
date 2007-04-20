<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>Comment Mentor</title>
<script type="text/javascript">
function verify() {
var themessage = "You are required to complete the following fields: ";
if (document.form.name.value=="") {
themessage = themessage + " - Name";
}
if (document.form.school.value=="") {
themessage = themessage + " -  School";
}
if (document.form.email.value=="") {
themessage = themessage + " -  E-mail";
}
//alert if fields are empty and cancel form submit
if (themessage == "You are required to complete the following fields: ") {
	var x = document.forms[0].email.value;
	var filter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
	if (filter.test(x));
	else {
		alert('Email address is NOT valid!');
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
	 <td>[<a href="recoverPasswordForm.php">Password Recovery</a>] </td>
   </tr>
  </table>

<br/><br/>

  <table>
   <tr>
     <td align="center">To signup for an account, fill out the form below.<br>(all fields are required to register)</td>
   </tr>
  </table>
  
  <br>

   <form action="register.php" method="post" name="form">
	<table id="frame">
	 <tr>
	  <td>Name:</td>
	 <tr>
	  <td><input type="text" name="name"></td>
	 <tr>
	  <td>School:</td>
	 <tr>
	  <td><input type="text" name="school"></td>
	 <tr>
	  <td>Email:</td>
	 <tr>
	  <td><input type="text" name="email"></td> 
	 <tr>
      <td><input type="submit" name="submit" value="Register" onClick="return verify();"></td>
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