{literal}
<script type="text/javascript">
<!--
function verifyAction(action, name)
{
  return confirm("Are you sure you want to " + action + " the account for " + name);
}
//-->
</script>
{/literal}

<h1>Account Management</h1>

{if is_array($usersE) && count($usersE) > 0 }
<table class="data">
<tr>
  <th>Name<br/>E-mail<br/>School</th>
  <th class="mini">Account Type</th>
  <th class="mini">Dropbox</th>
  <th class="mini">Reports</th>
  <th class="mini">Usage</th>
  <th>Actions</th>
</tr>

{foreach from=$usersE item="u"}
<tr>
  <td class="left">{$u.name}<br/>{$u.email}<br/>{$u.school}</td>
  <td class="mini">{$u.acctType}</td>
  <td class="mini center"><a href="dropbox.php?{if $u.acctType == 'professor'}profId{else}userId{/if}={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Dropbox" /></a></td>
  <td class="mini center"><a href="reports.php?{if $u.acctType == 'professor'}profId{else}userId{/if}={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Reports" /></a></td>
  <td class="mini center"><a href="usage.php?userId={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Usage" /></a></td>
  <td>
    <a href="editAccount.php?userId={$u.userId}"><img src="img/icons/edit.png" alt="Edit" /></a>
    <a href="disableAccount.php?userId={$u.userId}&amp;rand={$rand}" onclick="return verifyAction('disable', '{$u.name}');"><img src="img/icons/lock.png" alt="Disable" /></a>
    <a href="deleteAccount.php?userId={$u.userId}&amp;rand={$rand}" onclick="return verifyAction('delete', '{$u.name}');"><img src="img/icons/delete.png" alt="Delete" /></a>
  </td>
</tr>
{/foreach}

</table>
{else}
<h5 class="center">No Results Found.</h5>
{/if}

{$usersELinks}

<br />

<h1>Disabled Accounts</h1>

{if is_array($usersD) && count($usersD) > 0 }
<table class="data">
<tr>
  <th>Name (School)</th>
  <th class="mini">Account Type</th>
  <th class="mini">Reports</th>
  <th class="mini">Usage</th>
  <th>Actions</th>
</tr>

{foreach from=$usersD item="u"}
<tr>
  <td class="left">{$u.name}<br/>({$u.school})  </td>
  <td class="mini">{$u.acctType}</td>
  <td class="mini center"><a href="reports.php?profId={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Reports" /></a></td>
  <td class="mini center"><a href="usage.php?userId={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Usage" /></a></td>
  <td>
    <a href="editAccount.php?userId={$u.userId}"><img src="img/icons/edit.png" alt="Edit" /></a>
    <a href="enableAccount.php?userId={$u.userId}&amp;rand={$rand}" onclick="return verifyAction('enable', '{$u.name}');"><img src="img/icons/unlock.png" alt="Enable" /></a>
    <a href="deleteAccount.php?userId={$u.userId}&amp;rand={$rand}" onclick="return verifyAction('delete', '{$u.name}');"><img src="img/icons/delete.png" alt="Delete" /></a>
  </td>
</tr>
{/foreach}

</table>
{else}
<h5 class="center">No Results Found.</h5>
{/if}

{$usersDLinks}
