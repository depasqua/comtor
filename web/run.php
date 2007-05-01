<?
session_start();
//check for session id
if(!isset($_SESSION['userID'])) {
	include("redirect.php");
}

//set id of current user
$userID = $_SESSION['userID'];

//directory
include("directory.php");

//store IP address of host
$ip = $_SERVER['REMOTE_ADDR'];

//store information about jar file
$fileSize = $_FILES['file']['size'];
$fileName = str_replace(" ", "", $_FILES['file']['name']);

//message is used to display errors
$message = "";

//check to make sure the file is present
if(isset($_FILES['file']) == false OR $_FILES['file']['error'] == UPLOAD_ERR_NO_FILE){
	$message = "You need to upload a jar file!<br>";
}

//check file extension
function file_extension($filename)
{
    return end(explode(".", $filename));
}
$ext = file_extension($fileName);
if($ext != jar){
	$message = "You need to upload a jar file!<br>";
}

//check to make sure at least one doclet was selected
if(isset($_POST['doclet']) == false){
	$message = $message . "You need to select at least one analyzer!";
}

//if no errors
if($message == "")
{
	//the tmp_name begins with '/tmp/' so only grab the remaining substring
	$tempFileName = substr($_FILES['file']['tmp_name'], 5);
	
	//upload the jar file
	move_uploaded_file($_FILES['file']['tmp_name'], $dir . $fileName);
	
	//generate list of doclets selected (Doclets.txt)
	$myFile = $dir . $tempFileName . ".txt";
	$fh = fopen($myFile, 'a');
	
	if(isset($_POST['doclet'])){
		$doclet = $_POST['doclet'];
		$count = count($doclet);
		$i = 0;
		
		while ($i < $count){
			$nextDoclet = $doclet[$i] . "\n";
			fwrite($fh, $nextDoclet);
			$i++;
		}
	}
	fclose($fh);
	
	//starts a script to run javadoc
	exec($dir . "scripts/javadoc.sh " . $tempFileName . " " . $fileName . " " . $userID);
	
	//display report
	header('Content-Type: text/html; charset=ISO-8859-1');
	readfile($dir . $tempFileName . '/ComtorReport.php');
	exit;
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
  <td align="center">
	<? echo $message; ?><br><a href="index.php">Go back</a>.
  </td>
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