{if !isset($courseId) }
  <h1>Course Not Found</h1>
{else}
  <h1>Course Management</h1>
  <h4>{$course.section}: {$course.name}</h4>
  <h6>Professor:</h6> {$course.profName}<br/>
  <h6>Semester:</h6> {$course.semester}<br/>

  <div class="center gapBelowSmall">
    <a href="reports.php?courseId={$courseId}"><img src="img/icons/magnifying_glass.png" alt="View All Reports" /></a> View All Course Reports
  </div>

  {if is_array($students) && count($students) > 0 }
    {* Determine redirect location *}
    {assign var='loc' value='courseManage.php?courseId='|cat:$courseId}
    {assign var='loc' value=$loc|urlencode}

    <table class="data">
    <tr style="border-bottom: thin solid black">
      <th style="width: 20%">Name</th>
      <th style="width: 20%">E-mail Address</th>
      <th style="text-align:right; width:12%">Submissions</th>
      <th style="text-align:right; width:7%">Grade</th>
      <th style="width: 45%">Actions</th>
    </tr>
    {foreach from=$students item="s"}
      <tr style="border-bottom: thin solid black">
        <td class="left">{$s.name}</td>
        <td class="left"><a href="{$s.email}">{$s.email}</a></td>
        <td class="right small">{$s.submissions}</td>
        <td class="right small">N/A</td>
        <td>
          <a href="dropbox.php?userId={$s.userId}&amp;courseId={$courseId}" class="noLine">View Dropbox</a> | 
          <a href="reports.php?userId={$s.userId}&amp;courseId={$courseId}" class="noLine">View Reports</a> | 
          <a href="courseDrop.php?userId={$s.userId}&amp;courseId={$courseId}&amp;rand={$rand}&amp;loc={$loc}" class="noLine">Drop Student</a>
        </td>
      </tr>
    {/foreach}
    </table>
  {else}
    <div class="center">No students enrolled in this course.</div>
  {/if}

  <div class="center">
    <a href="findStudents.php?courseId={$courseId}">Enroll Additional Students</a>
  </div>
{/if}
