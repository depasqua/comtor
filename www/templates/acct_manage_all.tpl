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

{literal}
<script type="text/javascript">
// Function loaded on pageload.
$(document).ready(function() {
    // Deselects on load.
    $('input[name="user"]').attr('checked', false);
    
    // When #userSelect selected function initiates.
    $('#userSelect').change(function() {
        
        // Removes selection styling from other options.
        $("*").removeClass("selected")
            
        //Applys styling to parent row.
        $('input[name=user]:checked', '#userSelect').parents("tr").addClass("selected");
        
        // Puts selected table value into variable.
        $user = $('input[name=user]:checked', '#userSelect').val();
        
        // Splits the value.
        var infoArray = $user.split(',');
        
        // Modifies the page function links based on the input data.
        $("a.selectLink").each(function() {
            var _href = $(this).attr("href").split('?')[0] + '?';
            
            if($(this).hasClass('profCheck') && infoArray[1] == "profId=")
                $(this).attr("href", _href + infoArray[1] + infoArray[2]);
            else
                $(this).attr("href", _href + "userId=" + infoArray[2]);
                
            if($(this).hasClass('important'))
                $(this).attr("href", $(this).attr("href") + "&rand=" + infoArray[3]);
        });
        
        $("a.userMail").attr("href", "mailto:" + infoArray[0]);
    });
});

</script>
{/literal}

<h1>Account Management</h1>

{if is_array($usersE) && count($usersE) > 0 }
<div id="accountNav">
	<div id="views">
		<ul>
			<li class="button" role="button"><a class="leftRound selectLink profCheck" href="dropbox.php?">Dropbox</a></li>
			<li class="button" role="button"><a class="selectLink profCheck" href="reports.php?">Report</a></li>
			<li class="button" role="button"><a class="rightRound selectLink" href="usage.php?">Usage</a></li>
		</ul>
	</div>
	<div id="actions">
		<ul>
			<li class="button" role="button"><a class="leftRound selectLink" href="editAccount.php?">Edit</a></li>
			<li class="button" role="button"><a class="selectLink important" href="disableAccount.php?">Disable</a></li>
			<li class="button" role="button"><a class="selectLink important" href="deleteAccount.php?">Delete</a></li>
			<li class="button" role="button"><a class="rightRound userMail" href="#">Mail</a></li>
		</ul>
	</div>
</div>
<form id="userSelect">
<table class="data">

<tr>
	<th class="mini"></th>
  <th class="medium">Name</th>
  <th class="small">Account Type</th>
  <th class="large">School</th>
  <!--<th class="mini">Dropbox</th>
  <th class="mini">Reports</th>
  <th class="mini">Usage</th>
  <th>Actions</th>-->
</tr>

{assign var='name' value='even'}
{foreach from=$usersE item="u"}
{if $name == "even"}
	{assign var='name' value='odd'}
<tr class="even">
{else}
	{assign var='name' value='even'}
<tr class="odd">
{/if}
	<td class="selector mini"><input type="radio" name="user" value="{$u.email},{if $u.acctType == 'professor'}profId{else}userId{/if}=,{$u.userId},{$rand}" /></td>
  <td class="medium">{$u.name}</td>
  <td class="small">{$u.acctType}</td>
  <td class="large">Not working{$u.school}</td>
  <!--<td class="mini center"><a href="dropbox.php?{if $u.acctType == 'professor'}profId{else}userId{/if}={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Dropbox" /></a></td>
  <td class="mini center"><a href="reports.php?{if $u.acctType == 'professor'}profId{else}userId{/if}={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Reports" /></a></td>
  <td class="mini center"><a href="usage.php?userId={$u.userId}"><img src="img/icons/magnifying_glass.png" alt="View Usage" /></a></td>
  <td class="medium">
    <a href="editAccount.php?userId={$u.userId}"><img src="img/icons/edit.png" alt="Edit" /></a>
    <a href="disableAccount.php?userId={$u.userId}&amp;rand={$rand}" onclick="return verifyAction('disable', '{$u.name}');"><img src="img/icons/lock.png" alt="Disable" /></a>
    <a href="deleteAccount.php?userId={$u.userId}&amp;rand={$rand}" onclick="return verifyAction('delete', '{$u.name}');"><img src="img/icons/delete.png" alt="Delete" /></a>
    <a href="mailto:{$u.email}"><img src="img/icons/email.png" alt="Email" /></a>
  </td>-->
</tr>
{/foreach}

</table>
</form>
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
