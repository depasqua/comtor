{literal}
<script type='text/javascript'>
<!--

function verifyUserEventAction(action)
{
  return confirm("Are you sure you want to " + action + " this user event.");
}

//-->
</script>
{/literal}

<h3>{$name}</h3>
<h6>E-Mail Address: </h6>{$email}<br/>
<h6>School: </h6>{$school}<br/>

{if isset($course)}
  <h3>{$course.section}: {$course.name}</h3>
  {if $smarty.session.acctType != "professor" }
    <h6>Professor: </h6>{$course.profName}<br/>
  {/if}
  <h6>Semester: </h6>{$course.semester}<br/>
{/if}

{include file="doclet_usage.tpl"}

{if !isset($smarty.get.courseId) && $smarty.session.acctType != 'professor'}
<div class="center gapBelowSmall">
  {if $smarty.get.grp != "course"}
    <a href="reports.php?grp=course">Group by Course</a>
  {else}
    <a href="reports.php">Show without Groups</a>
  {/if}
</div>
{/if}

{foreach from=$eventGroups item="grp"}
{if isset($grp.course)}
  <a href='#course{$grp.course.courseId}'>{$grp.course.section}: {$grp.course.name}</a><br/>
{/if}
{/foreach}

{foreach from=$eventGroups item="grp"}
{if isset($grp.name)}
  {if isset($grp.course)}
    <h3 id='course{$grp.course.courseId}'>{$grp.course.section}: {$grp.course.name}</h3>
    {include file="doclet_usage.tpl" doclets=$grp.doclets}
  {else}
    <h3>{$grp.name}</h3>
  {/if}
{/if}
{include file="reports_table.tpl" userEvents=$grp.userEvents }
{foreachelse}
{include file="reports_table.tpl" userEvents=$userEvents }
{/foreach}
