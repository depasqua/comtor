<?
session_start();
if(!isset($_SESSION['userID'])) {
	header("Location: http://csjava/~brigand2/loginForm.php");
	exit;
}

$docletList = fopen('doclets.txt', 'r');

$i = 0;
while(!feof($docletList)) {
	$docletName = fgets($docletList);
	$docletsArray[$i] = $docletName;
	$i += 1;
}

fclose($docletList);
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
<style type=text/css>
#doclet{ font-weight:bold}
#description{ font-style:italic; padding-bottom:5px}
</style>
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

<form action="run.php" method="post" enctype="multipart/form-data" name="form" onSubmit="return verify()">
<table id="frame" cellpadding="0" cellspacing="3" border="0">
 <tr>
  <td>
   <input type="file" name="file" size="30"/>
  </td>
  <td>
   <select name="sourceType">
    <option>Java Source Jar</option>
    <option disabled="disabled">C Source</option>
    <option disabled="disabled">Bash Source</option>
    <option disabled="disabled">Pascal Source</option>
   </select>
  </td>
 </tr>
</table>
	
<table id="frame" cellpadding="0" cellspacing="5" border="0">
<? foreach ($docletsArray as $doclet){ 
	$displayName = strtok($doclet, "\t");
	$realName = strtok("\t");
	$description = strtok("\t");
?>
 <tr>
  <td valign="top">
   <input type="checkbox" name="doclet[]" value="<? echo $realName; ?>"/>
  </td>
  <td>
   <div id="doclet"> <? echo $displayName ?> </div><div id="description"> <? echo $description ?> </div>
  </td>
 </tr>
<? } ?>
 <tr>
  <td>&nbsp;</td>
  <td>
   <input type="submit" name="submit" value="Analyze Comments">
   <input type="Reset" value="Reset"/>
  </td>
 </tr>
</table>
</form>

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