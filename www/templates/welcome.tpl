<div class='center'>Welcome to COMTOR.  Please use the links to the left to navigate the different pages.</div>

{if isset($requests) && is_array($requests) }
<h3>Requests</h3>
  {foreach from=$requests item="req"}
    {if $req.num > 0}
      {$req.name}: <span class="requestsPending">{$req.num}</span><br/>
    {else}
      {$req.name}: {$req.num}<br/>
    {/if}
  {/foreach}
{/if}

<h3>Courses</h3>
{if is_array($courses) && count($courses) > 0 }
  {foreach from=$courses item="c"}
    <h4>
      <a href="{$url|default:'courses.php'}?courseId={$c.courseId}">{$c.section}: {$c.name} ({$c.semester})</a>
      {if isset($c.numStudents) && isset($c.numReports)}
        ({$c.numStudents} students, {$c.numReports} reports)
      {/if}
    </h4>
    {* Most Active Students *}
    {if isset($c.mostActiveStudents) && is_array($c.mostActiveStudents)}
      <table class="data">
        <tr>
          <th>Student</th>
          <th>Number of Submissions</th>
        </tr>
        {foreach from=$c.mostActiveStudents item="s"}
        <tr>
          <td><a href="dropbox.php?userId={$s.userId}&amp;courseId={$c.courseId}">{$s.name}</a></td>
          <td>{$s.numReports}</td>
        </tr>
        {/foreach}
      </table>
    {/if}
  {/foreach}
{elseif $resultMsg|default:true}
  <h5 class="center">No Results Found</h5>
{/if}
