<?
session_start();
//check for session id
if(!isset($_SESSION['userID']))
{
  header("Location: http://csjava/~sigwart4/loginForm.php");
  exit;
}
?>
<?php
  function headFunction()
  {
?>
<style type=text/css>
#doclet{ font-weight:bold}
#description{ font-style:italic; padding-bottom:5px}
</style>
<?php
  }
?>

<?php include_once("header.php"); ?>

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
<?php
/*
   //tokenize the text file of doclets
  foreach ($docletsArray as $doclet){
  $displayName = strtok($doclet, "\t");
  $realName = strtok("\t");
  $description = strtok("\t");
*/
// Get list of doclets and descriptions
// Connect to database
include("connect.php");

// Get report types from database
$query = "SELECT * FROM reports";
$result = mysql_query($query);

// Process results
while ($result && $row = mysql_fetch_assoc($result))
{
  $displayName = $row['reportName'];
  $realName = $row['javaName'];
  $description = $row['reportDescription'];

?>
 <tr>
  <td valign="top">
   <input type="checkbox" name="doclet[]" value="<? echo $realName; ?>"/>
  </td>
  <td>
   <div id="doclet"> <? echo $displayName ?> </div><div id="description"> <? echo $description ?> </div>
  </td>
 </tr>
<?php
  }
  // Close MySQL database
  mysql_close();
?>
 <tr>
  <td>&nbsp;</td>
  <td>
   <input type="submit" name="submit" value="Analyze Comments">
   <input type="Reset" value="Reset"/>
  </td>
 </tr>
</table>
</form>

<?php include_once("footer.php"); ?>
