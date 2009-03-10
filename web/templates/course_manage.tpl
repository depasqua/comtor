{if !isset($courseId) }
  <h1>Course Not Found</h1>
{else}
  <h1>Course Management</h1>
  <h4>{$course.section}: {$course.name}</h4>
  <h6>Professor:</h6>{$course.profName}<br/>
  <h6>Semester:</h6>{$course.semester}<br/>

  <div class="center gapBelowSmall">
    <a href="reports.php?courseId={$courseId}"><img src="img/icons/magnifying_glass.png" alt="View All Reports" /></a> View All Course Reports
  </div>

  {if is_array($students) && count($students) > 0 }
    {* Determine redirect location *}
    {assign var='loc' value='courseManage.php?courseId='|cat:$courseId}
    {assign var='loc' value=$loc|urlencode}

    <table class="data">
    <tr>
      <th>Name</th>
      <th>E-mail Address</th>
      <th class="small">Submissions</th>
      <th class="small">Grade</th>
      <th class="large">Actions</th>
    </tr>
    {foreach from=$students item="s"}
      <tr>
        <td class="left">{$s.name}</td>
        <td class="left">{$s.email}</td>
        <td class="right small">{$s.submissions}</td>
        <td class="right small">N/A</td>
        <td>
          <a href="dropbox.php?userId={$s.userId}&amp;courseId={$courseId}" class="noLine"><img src="img/icons/magnifying_glass.png" alt="View Dropbox" /> View Dropbox</a><br/>
          <a href="reports.php?userId={$s.userId}&amp;courseId={$courseId}" class="noLine"><img src="img/icons/magnifying_glass.png" alt="View Reports" /> View All Reports</a><br/>
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
