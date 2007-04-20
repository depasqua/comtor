<?
session_start();
if(!isset($_SESSION['userID'])) {
	//redirect to comment mentor
	header("Location: http://csjava/~brigand2/loginForm.php");
	exit;
}
$userID = $_SESSION['userID'];

if(isset($_GET['id'])){	
//connect to database
mysql_connect('localhost', 'brigand2', 'joeBrig');
mysql_select_db('comtor');

mysql_query("UPDATE users SET acctStatus='disabled' WHERE userID='$userID'");
$message = "Your account has been deleted!";
session_destroy();
}
else{
$message = "Are you sure you want to request account deletion? <a href=\"deleteAccount.php?id=$userID\">Yes</a> <a href=\"index.php\">No</a>";

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
	 <td>[<a href="changePasswordForm.php">Change Password</a>] </td>
	 <td>[<a href="report.php">View Report</a>] </td>
	 <td>[<a href="deleteAccount.php">Delete Account</a>] </td>
	 <td>[<a href="logout.php">Logout</a>] </td>
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