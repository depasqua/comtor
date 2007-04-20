<?
if(!isset($_POST['submit'])){	
	header("Location: http://csjava/~brigand2/index.php");
	exit;
}

//form data
$email = $_POST['email'];
$password = stripslashes($_POST['password']);
		
//connect to database
mysql_connect('localhost', 'brigand2', 'joeBrig');
mysql_select_db('comtor');
		
//validate email and password
$result = mysql_query("SELECT * FROM users WHERE email='$email'");
if (mysql_num_rows($result) == 0) {
	$message = "Username does not exist!";
}
else {
$row = mysql_fetch_array($result);
$userID = $row['userID'];
$acctStatus = $row['acctStatus'];

if($acctStatus == enabled)
{		
	$cryptPassword = crypt($password, 'cm');
	if($cryptPassword == $row['password'])
	{
		//if first login, set validated date and time
		if($row['validatedDT'] == NULL)
		{
			mysql_query("UPDATE users SET validatedDT=NOW(), lastLogin=NOW() WHERE userID='$userID'");
		}
		else
		{
			mysql_query("UPDATE users SET lastLogin=NOW() WHERE userID='$userID'");
		}
			
		session_start();	
		$_SESSION['userID'] = $userID;
		//redirect to comment mentor
		header("Location: http://csjava/~brigand2/index.php");
		exit;
	}
	else
	{
		$message = "Incorrect password!";
	}
}
else{
$message = "Username does not exist!";
}
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
     <td align="center">
		<? echo $message; ?><br><a href="loginForm.php">Back to login</a>.
	 </td>
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