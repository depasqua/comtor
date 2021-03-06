<h1>Course Dropbox</h1>

{* Determine redirect location *}
{assign var='loc' value='dropbox.php?'}
{assign var='loc' value=$loc|cat:'userId='|cat:$smarty.get.userId|cat:'&courseId='|cat:$smarty.get.courseId}
{assign var='loc' value=$loc|urlencode}

{if $submittable|default:false}
  <a href="submit.php">Submit Code for an Assignment</a><br/>
{/if}
{if $editable|default:false}

  <a href="assignment_add.php">Add New Assignment</a><br/>

  {if count($students) > 0}
  <h3>Students</h3>
  <select onchange="window.location = '?courseId={$smarty.get.courseId}&amp;userId='+this.value">
    <option value="">No Student</option>
    {foreach from=$students item="stud"}
      <option value="{$stud.userId}" {if $smarty.get.userId == $stud.userId}selected="selected"{/if}>{$stud.name}</option>
    {/foreach}
  </select>
  {/if}

  <div class="noteFull">
    Please note that students may only submit files for assignments.  If you wish to allow them to submit test files that are not associated with a specific assignment, you may create a general assignment into which students may submit.
  </div>
{/if}

{if count($assignments) > 0 }
{assign var="format" value="n/j/Y g:i A"}
<table class="data">
  <tr>
    <th>Item</th>
    <th>Date Submitted</th>
    <th>Comment</th>
    <th>Actions</th>
  </tr>
  {foreach from=$assignments item="a"}
    <tr>
      <td colspan="4" class="subheading">
        {$a.name}
        ({strtodate format=$format date=$a.openTime} - {strtodate format=$format date=$a.closeTime})
        {if $editable|default:false}
          <a href="assignment_add.php?state=edit&amp;rand={$rand}&amp;assignment_id={$a.assignmentId}">Edit</a> |
          <a href="assignment_delete.php?rand={$rand}&amp;assignment_id={$a.assignmentId}">Delete</a>
        {/if}
      </td>
    </tr>
    {foreach from=$a.submissions item="s"}
      <tr {if $s.compilationError}class="errorText"{/if}>
        <td>{$s.name}</td>
        <td>{strtodate format=$format date=$s.submission_date}</td>
        <td>
          <div style="text-align: left;">{$s.comment}</div>
          {if $editable|default:false}
            {if !empty($s.comment)}<br/>{/if}
            <a href="assignment_comment.php?userEventId={$s.userEventId}&amp;loc={$loc}">{if empty($s.comment)}Add{else}Edit{/if} Comment</a>
          {/if}
        </td>
        <td>
          <a href="reports.php?userEventId={$s.userEventId}">View</a> |
          <a href="reports.php?userEventId={$s.userEventId}&amp;dl=T">Download</a> 
        </td>
      </tr>
    {/foreach}
  {/foreach}
</table>
{else}
  <h5 class="center">No Results Found.</h5>
{/if}
