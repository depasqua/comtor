<?php
$acctTypes = "admin";
require_once("loginCheck.php");

function headFunction()
{
?>
<script type='text/javascript'>
<!--
function verifyAction(action, name)
{
  return confirm("Are you sure you want to " + action + " the account for " + name);
}
//-->
</script>
<?php
}
?>
<?php include_once("header.php"); ?>

<?php

//Define the default and maximum number of users to be shown
define("DEFAULT_LIST_TOTAL", 25);
define("MAX_LIST_TOTAL", 100);

?>

<h1>Account Management</h1>

<table class='data'>
<tr>
  <th>Name (School)</th>
  <th class='mini'>Account Type</th>
  <th class='mini'>Reports</th>
  <th class='mini'>Usage</th>
  <th>Actions</th>
</tr>
<?
// Connect to database
include("connect.php");

// Random string added to important links for security
$md5Rand = md5(session_id());

// Check if there is a limit set for enabled accounts
if (!isset($_GET['lowerEn']) || !is_numeric($_GET['lowerEn']))
  $_GET['lowerEn'] = 0;
if (!isset($_GET['totalEn']) || !is_numeric($_GET['totalEn']))
  $_GET['totalEn'] = DEFAULT_LIST_TOTAL;
// Check that total is less than the maximum valid
else if ($_GET['totalEn'] > MAX_LIST_TOTAL)
  $_GET['totalEn'] = MAX_LIST_TOTAL;

// Check if there is a limit set for disabled accounts
if (!isset($_GET['lowerDis']) || !is_numeric($_GET['lowerDis']))
  $_GET['lowerDis'] = 0;
if (!isset($_GET['totalDis']) || !is_numeric($_GET['totalDis']))
  $_GET['totalDis'] = DEFAULT_LIST_TOTAL;
// Check that total is less than the maximum valid
else if ($_GET['totalDis'] > MAX_LIST_TOTAL)
  $_GET['totalDis'] = MAX_LIST_TOTAL;


// List accounts that are currently enabled
if ($users = getUsers(array("userId", "name", "school", "acctType"), "enabled", "all", $_GET['lowerEn'], $_GET['totalEn']))
{
  foreach($users as $user)
  {
    $userId = $user['userId'];

    //list of options for user (view, admin, delete, disable)
    echo "<tr>\n";
    echo "  <td class='left'>\n";
    echo "    {$user['name']}<br/>({$user['school']})";
    echo "  </td>\n";
    echo "  <td class='mini'>{$user['acctType']}</td>\n";

    // View reports for a student
    if ($user['acctType'] == "student")
      echo "  <td class='mini center'><a href='reports.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Reports' /></a></td>\n";
    else if ($user['acctType'] == "professor")
      echo "  <td class='mini center'><a href='reports.php?profId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Reports' /></a></td>\n";
    else
      echo "  <td class='mini center'>N/A</td>\n";


    echo "  <td class='mini center'><a href='usage.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Usage' /></a></td>\n";
    echo "  <td>\n";
    echo "    <a href='editAccount.php?userId={$userId}'><img src='img/icons/edit.gif' alt='Edit' /></a>\n";
    echo "    <a href='disableAccount.php?userId={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"disable\", \"{$user['name']}\");'><img src='img/icons/lock.gif' alt='Disable' /></a>\n";
    echo "    <a href='deleteAccount.php?userId={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"delete\", \"{$user['name']}\");'><img src='img/icons/delete.gif' alt='Delete' /></a>\n";
    echo "  </td>\n";
    echo "</tr>\n";
  }
}
?>
</table>

<?php

// Add links to view other courses if there are any
$numUsers = getNumUsers("enabled");

require_once("generalFunctions.php");

listPages($_GET['lowerEn'], $_GET['totalEn'], $numUsers, "lowerEn", "totalEn", "lowerDis={$_GET['lowerDis']}&amp;totalDis={$_GET['totalDis']}");

?>

<?
// List accounts that are currently disabled
if ($users = getUsers(array("userId", "name", "school", "acctType"), "disabled", "all", $_GET['lowerDis'], $_GET['totalDis']))
{
  // Print Table headings
  echo "<br />\n";
  echo "<h1>Disabled Accounts</h1>\n";
  echo "<table class='data'>\n";
  echo "<tr>\n";
  echo "  <th>Name (School)</th>\n";
  echo "  <th class='mini'>Account Type</th>\n";
  echo "  <th class='mini'>Reports</th>\n";
  echo "  <th class='mini'>Usage</th>\n";
  echo "  <th>Actions</th>\n";
  echo "</tr>\n";

  foreach($users as $user)
  {
    $userId = $user['userId'];

    //list of options for user (view, admin, delete, enable)

    echo "<tr>\n";
    echo "  <td>{$user['name']} ({$user['school']})</td>\n";
    echo "  <td class='mini'>{$user['acctType']}</td>\n";
    echo "  <td class='mini center'><a href='reports.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Reports' /></a></td>\n";
    echo "  <td class='mini center'><a href='usage.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Usage' /></a></td>\n";
    // Actions
    echo "  <td>\n";
    echo "    <a href='editAccount.php?userId={$userId}'><img src='img/icons/edit.gif' alt='Edit' /></a>\n";
    echo "    <a href='enableAccount.php?userId={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"enable\", \"{$user['name']}\");'><img src='img/icons/unlock.gif' alt='Enable' /></a>\n";
    echo "    <a href='deleteAccount.php?userId={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"delete\", \"{$user['name']}\");'><img src='img/icons/delete.gif' alt='Delete' /></a>\n";
    echo "  </td>\n";
    echo "</tr>\n";
  }

  // Close the table
  echo "</table>\n";
}


// Add links to view other courses if there are any
$numUsers = getNumUsers("disabled");
listPages($_GET['lowerDis'], $_GET['totalDis'], $numUsers, "lowerDis", "totalDis", "lowerEn={$_GET['lowerEn']}&amp;totalEn={$_GET['totalEn']}");

?>

<?php include_once("footer.php"); ?>
