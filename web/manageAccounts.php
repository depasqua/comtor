<?php
$adminOnly = true;
require_once("loginCheck.php");
?>
<?php
  function headFunction()
  {
?>
<style type=text/css>
#class{font-size: 18px; font-weight: bold; padding-bottom:20px;}
</style>
<?php
}
?>

<?php include_once("header.php"); ?>

<div id="class" align="center">--Account Management--</div>

<table cellpadding="2">
<?
//connect to database
include("connect.php");

//list accounts that are currently enabled
$query = mysql_query("SELECT * FROM users WHERE acctStatus='enabled'");
while($row = mysql_fetch_assoc($query))
{
  $userID = $row['userID'];

  //list of options for user (view, admin, delete, disable)
  ?><tr><td><? if($row['acctType'] == "admin"){echo "*";} echo $row['name'] . " (" . $row['school'] . ")"; ?></td>
  <td align="left"><a href="reports.php?id=<? echo $userID; ?>">[view]</a></td>
  <td align="left"><a href="changeAcctType.php?id=<? echo $userID; ?>">[admin]</a></td>
  <td align="left"><a href="deleteAccount.php?id=<? echo $userID; ?>">[delete]</a></td>
  <td align="left"><a href="disableAccount.php?id=<? echo $userID; ?>">[disable]</a></td></tr><?
}
?>
</table>

<br><br><br><div id="class" align="center">--Disabled Accounts--</div>

<table cellpadding="2">
<?
//list accounts that are currently disabled
$query = mysql_query("SELECT userID, name, school FROM users WHERE acctStatus='disabled'");
while($row = mysql_fetch_assoc($query))
{
  $userID = $row['userID'];

  //list of options for user (view, admin, delete, enable)
  ?><tr><td><? echo $row['name'] . " (" . $row['school'] . ")"; ?></td>
  <td align="left"><a href="reports.php?id=<? echo $userID; ?>">[view]</a></td>
  <td align="left"><a href="changeAcctType.php?id=<? echo $userID; ?>">[admin]</a></td>
  <td align="left"><a href="deleteAccount.php?id=<? echo $userID; ?>">[delete]</a></td>
  <td align="left"><a href="enableAccount.php?id=<? echo $userID; ?>">[enable]</a></td></tr><?
}
?>
</table>

<?php include_once("footer.php"); ?>
