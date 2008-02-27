<?php
$adminOnly = true;
require_once("loginCheck.php");
?>
<?php
  function headFunction()
  {
?>
<style type=text/css>
#body{width:65%;}
#class{font-size: 18px; font-weight: bold}
</style>
<?php
}
?>

<?php include_once("header.php"); ?>

<div id="body">
<div id="class">--System Usage--</div>
<?
//connect to database
include("connect.php");

//display the list of doclets
$query = mysql_query("SELECT * FROM reports");
while($row = mysql_fetch_assoc($query))
{
	$name = $row['reportName'];
	$id = $row['reportID'];
	
	echo "<br>" . $name . " - ";
	
	//calculate the number of times each doclet was run
	$query2 = mysql_query("SELECT id FROM masterDoclets WHERE docletReportId='$id'");
	$numRows = mysql_num_rows($query2);
	echo "selected " . $numRows . " times<br>";
}
?>
</div>

<?php include_once("footer.php"); ?>
