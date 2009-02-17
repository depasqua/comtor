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

<h1>Schools</h1>

<a href="?state=new">Add New School</a>

<br/>

{if is_array($schools) && count($schools) > 0 }
<table class="data">
<tr>
  <th>School</th>
  <th>Actions</th>
</tr>

{foreach from=$schools item="sch"}
<tr>
  <td class="left">{$sch.school}</td>
  <td>
    <a href="?schId={$sch.schoolId}&amp;state=edit"><img src="img/icons/edit.gif" alt="Edit" /></a>
  </td>
</tr>
{/foreach}

</table>
{else}
<h5 class="center">No Results Found.</h5>
{/if}
