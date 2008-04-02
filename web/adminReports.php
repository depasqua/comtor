<?php
$acctTypes = "admin";
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

<?
//connect to database
include("connect.php");

/* Display use of each doclet */
if ($doclets = getDoclets())
{
  echo "<h1>Doclet Usage</h1>\n";
  foreach ($doclets as $doclet)
  {
    // Display name of report
    echo "<h6>{$doclet['reportName']}:</h6> ";

    // Calculate and display number of times the report was run
    $numRows = getDocletRuns($doclet['reportID']);
    echo "selected " . $numRows . " times<br/>\n";
  }
}

?>

<?php include_once("footer.php"); ?>
