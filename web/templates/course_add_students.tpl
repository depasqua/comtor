{literal}
<script type="text/javascript">
<!--
function verifyEnroll(name)
{
  return confirm("Are you sure you want to enroll " + name);
}
//-->
</script>
{/literal}

<h1>Possible Students for {$course.name}</h1>

{if is_array($users) && count($users) > 0 }
<table class="data">
<tr>
  <th>Name</th>
  <th>Actions</th>
</tr>

{* Determine redirect location *}
{assign var='loc' value='findStudents.php?courseId='|cat:$courseId}
{assign var='loc' value=$loc|urlencode}

{foreach from=$users item="u"}
<tr>
  <td class="left">{$u.name}</td>
  <td>
    <a href="courseEnroll.php?courseId={$courseId}&amp;userId={$u.userId}&amp;rand={$rand}&amp;loc={$loc}" onclick="return verifyEnroll('{$u.name}');">Enroll</a>
  </td>
</tr>
{/foreach}

</table>
{else}
<h5 class="center">No Results Found.</h5>
{/if}

{$usersLinks}
