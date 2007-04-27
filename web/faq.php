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

<table width="90%" cellpadding="1" cellspacing="1" border="0">
       <tr>
        <td align="left">
         <b>Installation Questions</b><br/> 
         <ol>
            <li>Where can I get Comment Mentor?
              <br/><br/>
              <i>You can download the <a href="comtor.tar.gz">latest version</a>.
              Sorry, we don't have public CVS access yet.</i>
              <br/>
            </li>
            <li>What are the installation requirements?
	      <br/><br/>
	      <i>You need to download and install the <a href="http://ant.apache.org/">Ant build tool</a> from the Apache Software Foundation. You will need a recent <a href="http://java.sun.com/">Java Development Kit</a> (CoMtor is developed with the JDK 5.x -- it should work).</i>
	      <br/>
	    </li>
            <li>How do I install Comment Mentor locally?
	      <br/><br/>
	      <i>
	        <ol>
		  <li> Download the tar.gz file </li>
		  <li> <code>tar -zxf comtor.tar.gz</code> </li>
		  <li> <code>cd comtor/code</code> </li>
		  <li> <code>ant</code> </li>
		</ol>
	      </i>
	      <br/>
	    </li>
            <li>How do I use the web interface/service?
	      <br/><br/>
	      <i>We are still working on it. Check back, and use the command line version in the meanwhile.</i>
	      <br/>
	    </li>
            <li>Can I run the web service locally?
	      <br/><br/>
	      <i>Sure -- once we write the code for it, we will package it
	         with the rest of the comtor download. You will probably
		 need to get familiar with downloading and installing a
		 servlet container such as Apache's 
		 <a href="http://tomcat.apache.org/">Tomcat</a>.
	      </i>
	      <br/>
	    </li>
          </ol>
          <b>General Questions</b><br/>
          <ol>
            <li>I don't like Java. What other languages are you going to support?</li>
            <li>I like Java -- what other languages are you planning to support?</li>
            <li>Are you guys inventing a comment language here?</li>
            <li>How is this different than source code annotation?</li>
            <li>How can I help?</li>
            <li>Is Comment Mentor FOSS (Free/Open Source Software)?
	      <br/><br/>
	        <i>Yes. Comment Mentor's code is licensed under the terms
		   of the <a href="http://www.gnu.org/licenses/gpl.txt">GNU GPL, version 2</a>.
		   The source code is distributed as the software download
		   itself, and you are free to make changes to the software,
		   as long as your changes are distributed under the GPL.
		</i>
	      <br/>
	    </li>
          </ol>
          <b>Technical Questions</b>
          <ol>
            <li>What features are you measuring?
             <br/><br/>
             <i>We've got your <a href="features.html">list of features right here</a>.</i><br/>
            </li>
          </ol>
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