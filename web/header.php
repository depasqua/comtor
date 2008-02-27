<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="icon" type="image/gif" href="/~sigwart4/favicon.gif" />
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="/~sigwart4/favicon.ico" />


<meta name="generator" content="HTML Tidy, see www.w3.org">
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<!-- TemplateBeginEditable name="doctitle" -->
<title>Comment Mentor</title>
<!-- TemplateEndEditable -->
<!-- TemplateBeginEditable name="head" -->

<?php
// Output content of headFunction() if it is defined
if (function_exists("headFunction"))
  headFunction();
?>

<!-- TemplateEndEditable -->
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

<!-- TemplateBeginEditable name="EditRegion" -->
