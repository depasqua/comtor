{if count($userEvents) > 0 }
<table class="data">
  <tr>
    <th class="mini">View</th>
    {if $showuser|default:false}<th>User</th>{/if}
    <th>Date</th>
    <th>Day</th>
    <th>Time</th>
    {if $deletable|default:false}
      <th class="mini">Delete</th>
    {/if}
  </tr>
  {foreach from=$userEvents item="e"}
  <tr>
    <td class="mini"><a href="reports.php?userEventId={$e.userEventId}&amp;userId={$e.userId}"><img src="img/icons/magnifying_glass.gif" alt="View Report"></a></td>
    {if $showuser|default:false}<td>{$e.user.name}</td>{/if}
    <td>{strtodate format="F j, Y" date=$e.dateTime}</td>
    <td>{strtodate format="l" date=$e.dateTime}</td>
    <td>{strtodate format="g:i:s A" date=$e.dateTime}</td>
    {if $deletable|default:false}
      <td><a href="userEventDelete.php?userEventId={$e.userEventId}&amp;rand={$rand}" onclick="return verifyUserEventAction('delete');"><img src="img/icons/delete.gif" alt="Delete Report"></a></td>
    {/if}
  </tr>
  {/foreach}
</table>
{else}
  <h5 class="center">No Results Found.</h5>
{/if}
