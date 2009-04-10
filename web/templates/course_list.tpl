{if isset($group_title)}
<h3>{$group_title}</h3>
{/if}

{if is_array($courses_all) && count($courses_all) > 0 }
<table class="data">
<tr>
  <th>Professor</th>
  <th>Course Section</th>
  <th>Course Name</th>
  <th>Semester</th>
  <th class="large">Comments</th>
  <th class="large">Actions</th>
</tr>

{foreach from=$courses_all item="c"}
<tr>
  <td>{$c.profName}</td>
  <td>{$c.section}</td>
  <td>{$c.name}</td>
  <td>{$c.semester}</td>
  <td class="large">{$c.comment}</td>
  {* Actions *}
  <td>
    {if in_array('edit', $c.actions)}
      <a href="courseEditForm.php?courseId={$c.courseId}"><img alt="Edit Course" src="img/icons/edit.png"/></a>
    {/if}
    {if in_array('manage', $c.actions)}
      <a href="courseManage.php?courseId={$c.courseId}"><img title="View Course" alt="View Course" src="img/icons/magnifying_glass.png"/></a>
    {/if}
    {if in_array('disable', $c.actions)}
      <a onclick="return verifyCourseAction('disable', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" href="disableCourse.php?courseId={$c.courseId}&amp;rand={$rand}"><img title="Disable" alt="Disable" src="img/icons/lock.png"/></a>
    {/if}
    {if in_array('enable', $c.actions)}
      <a onclick="return verifyCourseAction('enable', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" href="enableCourse.php?courseId={$c.courseId}&amp;rand={$rand}"><img title="Enable" alt="Enable" src="img/icons/unlock.png"/></a>
    {/if}
    {if in_array('delete', $c.actions)}
      <a onclick="return verifyCourseAction('delete', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" href="courseDelete.php?courseId={$c.courseId}&amp;rand={$rand}"><img title="Delete Course" alt="Delete Course" src="img/icons/delete.png"/></a>
    {/if}
    {if in_array('reports', $c.actions)}
      <a href="reports.php?courseId={$c.courseId}"><img src="img/icons/magnifying_glass.png" title="View Course Reports" alt="View Course Reports" /></a>
    {/if}
    {if in_array('drop', $c.actions)}
      <a href="courseDrop.php?courseId={$c.courseId}&amp;rand={$rand}" onclick="return verifyCourseAction('drop', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" >Drop</a>
    {/if}
    {if in_array('enroll', $c.actions)}
      <a href="courseEnroll.php?courseId={$c.courseId}&amp;rand={$rand}" onclick="getEnrollToken(this);" >Enroll</a>
    {/if}
    {if in_array('email', $c.actions)}
      <a href="course_email.php?courseId={$c.courseId}"><img src="img/icons/email.png" title="E-Mail Course Participants" alt="E-Mail" /></a>
    {/if}
  </td>
</tr>
{/foreach}

</table>
{elseif $resultMsg|default:true}
<h5 class="center">No Results Found</h5>
{/if}

{$pages|default:''}
