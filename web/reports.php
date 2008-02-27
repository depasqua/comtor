<?php require_once("loginCheck.php"); ?>
<?php
function headFunction()
{
?>
<style type=text/css>
#body{width:80%;}
#report{border:double; padding: 10px; font-size: 20px; text-align:left; margin-bottom:15px;}
#reportList {width:300px;  margin-left:auto; margin-right:auto;}
#class{font-size: 18px; font-weight: bold}
#method{padding-left:25px; padding-top:5px; font-size: 14px; font-style: italic}
#comment{padding-left:50px; font-size: 12px}
</style>
<?php
}
?>

<?php include_once("header.php"); ?>

<div id="body">
<?
//connect to database
include("connect.php");

//checks for admin permissions, else use current user id
if((isset($_GET['userId'])) && ($_SESSION['acctType'] == "admin"))
{
  $userID = $_GET['userId'];
}
else {
  $userID = $_SESSION['userID'];
}

//display current user information
$userInfo = mysql_query("SELECT * FROM users WHERE userID='$userID'");
$row = mysql_fetch_array($userInfo);
?><div id="reportList"><?
echo $row['name'] . "<br>";
echo $row['email'] . "<br>";
echo $row['school'] . "<br><br>";
?></div><?


// Array to store master report id and its time
$masters = array();

// Where part of query to display times each doclet was run
$where = "(";
// Find all master reports for the user
$result = mysql_query("SELECT id, dateTime FROM masterReports WHERE userID='$userID'");
$first = true;
while($row = mysql_fetch_assoc($result))
{
  $masterId = $row['id'];
  array_push($masters, array($masterId, $row['dateTime']));

  // Add OR if this is not the first condition
  if ($first)
    $first = false;
  else
    $where .= " OR ";

  $where .= "masterReportId=$masterId";
}

// This is going after another WHERE condition so add AND
if ($where != "(")
  $where = " AND " . $where . ")";
else
  $where = "";

// if there is no report selected, show list of user's reports
if(!isset($_GET['id'])) {
  ?><div id="class">--System Usage--</div><?
  //display name of report
  $query = mysql_query("SELECT * FROM reports");
  while($row = mysql_fetch_assoc($query))
  {
    $name = $row['reportName'];
    $id = $row['reportID'];

    echo "" . $name . " - ";

    // Calculate and display number of times the report was run
    $query2 = mysql_query("SELECT id FROM masterDoclets WHERE docletReportId='{$id}'{$where}");
    $numRows = mysql_num_rows($query2);
    echo "selected " . $numRows . " times<br>";
  }

  //display list of reports
  ?>
  <br><div id="class">--Reports--</div>
  <?php
  foreach($masters as $row)
  {
    $dateTime = $row[1];
    $displayDateTime = formatDateTime($dateTime);
    echo "<div id='reportList'>\n";
    echo "<a href=\"reports.php?id={$row[0]}\"> $displayDateTime </a><br>";
    echo "</div>\n";
  }
}
//if a report is selected, display report
else
{
  $masterId = $_GET['id'];

  // Check that masterId corresponds to this users report
  $result = mysql_query("SELECT userId FROM masterReports WHERE id='{$masterId}' AND userId='{$userID}'");
  if (mysql_num_rows($result) > 0)
  {
    $query = mysql_query("SELECT * FROM reports");
    while($row = mysql_fetch_assoc($query))
    {
      $reportID = $row['reportID'];
      $name = $row['reportName'];
      $description = $row['reportDescription'];

      // Find the id that links the masterReportId and docletReportId
      $result = mysql_query("SELECT id FROM masterDoclets WHERE masterReportId='{$masterId}' AND docletReportId='{$reportID}'");
      if ($info = mysql_fetch_assoc($result))
      {
        $linkId = $info['id'];

        $report = mysql_query("SELECT * FROM docletReports WHERE reportId='$linkId' ORDER BY attribute");
        if (mysql_num_rows($report) > 0) {
          ?><div id="report"><? echo $name . " (" . $description . ")";

          //checks the index to see whether the attribute is a class, method, or comment
          //the index is from the property list, examples shown below
          while($row = mysql_fetch_assoc($report)) {
            $index = $row['attribute'];
              //property list index - 011
              if(strlen($index) == 3) {
                ?><hr><div id="class"><? echo $row['value']; ?></div><?
              }
              //property list index - 011.002
              else if(strlen($index) == 7) {
                ?><div id="method"><? echo $row['value']; ?></div><?
              }
              //property list index - 001.002.a
              else if(strlen($index) > 7) {
                ?><div id="comment"><? echo $row['value']; ?></div><?
              }
          }
          echo "</div>\n";
        }
      }
    }
  }
}

//formate date
function formatDateTime($timestamp)
{
    $year=substr($timestamp,0,4);
    $month=substr($timestamp,5,2);
    $day=substr($timestamp,8,2);
    $hour=substr($timestamp,11,2);
    $minute=substr($timestamp,14,2);
    $second=substr($timestamp,17,2);
  $date = date("M d, Y -- g:i:s A", mktime($hour, $minute, $second, $month, $day, $year));
  RETURN ($date);
}
?>
</div>

<?php include_once("footer.php"); ?>
