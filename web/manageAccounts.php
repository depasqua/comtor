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

// List accounts that are currently enabled
if ($users = getUsers(array("userID", "name", "school", "acctType"), "enabled"))
{
  foreach($users as $user)
  {
    $userId = $user['userID'];

    //list of options for user (view, admin, delete, disable)
    echo "<tr>\n";
    echo "  <td class='left'>\n";
    echo "    {$user['name']}<br/>({$user['school']})";
    echo "  </td>\n";
    echo "  <td class='mini'>{$user['acctType']}</td>\n";
    echo "  <td class='mini center'><a href='reports.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Reports' /></a></td>\n";
    echo "  <td class='mini center'><a href='usage.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Usage' /></a></td>\n";
    echo "  <td>\n";
    echo "    <a href='editAccount.php?id={$userId}'><img src='img/icons/edit.gif' alt='Edit' /></a>\n";
    echo "    <a href='disableAccount.php?id={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"disable\", \"{$user['name']}\");'><img src='img/icons/lock.gif' alt='Disable' /></a>\n";
    echo "    <a href='deleteAccount.php?id={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"delete\", \"{$user['name']}\");'><img src='img/icons/delete.gif' alt='Delete' /></a>\n";
    echo "  </td>\n";
    echo "</tr>\n";
  }
}
?>
</table>

<?
// List accounts that are currently disabled
if ($users = getUsers(array("userID", "name", "school", "acctType"), "disabled"))
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
    $userId = $user['userID'];

    //list of options for user (view, admin, delete, enable)

    echo "<tr>\n";
    echo "  <td>{$user['name']} ({$user['school']})</td>\n";
    echo "  <td class='mini'>{$user['acctType']}</td>\n";
    echo "  <td class='mini center'><a href='reports.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Reports' /></a></td>\n";
    echo "  <td class='mini center'><a href='usage.php?userId={$userId}'><img src='img/icons/magnifying_glass.gif' alt='View Usage' /></a></td>\n";
    // Actions
    echo "  <td>\n";
    echo "    <a href='editAccount.php?id={$userId}'><img src='img/icons/edit.gif' alt='Edit' /></a>\n";
    echo "    <a href='enableAccount.php?id={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"enable\", \"{$user['name']}\");'><img src='img/icons/unlock.gif' alt='Enable' /></a>\n";
    echo "    <a href='deleteAccount.php?id={$userId}&amp;rand={$md5Rand}' onclick='return verifyAction(\"delete\", \"{$user['name']}\");'><img src='img/icons/delete.gif' alt='Delete' /></a>\n";
    echo "  </td>\n";
    echo "</tr>\n";
  }

  // Close the table
  echo "</table>\n";
}
?>

<?php include_once("footer.php"); ?>
