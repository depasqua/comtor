{* Online Users *}
<h3>Online Users</h3>

<table class="data">
<tr>
  <th>Name</th>
  <th>E-mail Address</th>
</tr> 
{foreach from=$users item="user"}
<tr>
  <td>{$user.name|htmlspecialchars}</td>
  <td>{$user.email|htmlspecialchars}</td>
</tr> 
{/foreach}
</table>
