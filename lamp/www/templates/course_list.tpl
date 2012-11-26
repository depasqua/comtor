{if isset($group_title)}
<h3>{$group_title}</h3>
{/if}

{if is_array($courses_all) && count($courses_all) > 0 }
<table class="data">
<tr>
  <th>Semester</th>
  <th>Course Section</th>
  <th class="medium">Course Name</th>
  <th>Professor</th>
  <th>Comments</th>
  <th>Actions</th>
</tr>

{foreach from=$courses_all item="c"}
<tr style="border-bottom: thin solid black">
  <td>{$c.semester}</td>
  <td>{$c.section}</td>
  <td>{$c.name}</td>
  <td>{$c.profName}</td>
  <td class="large">{$c.comment}</td>
  {* Actions *}
  <td>
    {if in_array('edit', $c.actions)}
      <a href="courseEditForm.php?courseId={$c.courseId}"><img alt="Edit Course" src="img/icons/edit.png"/></a>
    {/if}
    {if in_array('manage', $c.actions)}
      <a href="courseManage.php?courseId={$c.courseId}"><img alt="View Course" src="img/icons/magnifying_glass.png"/></a>
    {/if}
    {if in_array('disable', $c.actions)}
      <a onclick="return verifyCourseAction('disable', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" href="disableCourse.php?courseId={$c.courseId}&amp;rand={$rand}"><img alt="Disable" src="img/icons/lock.png"/></a>
    {/if}
    {if in_array('enable', $c.actions)}
      <a onclick="return verifyCourseAction('enable', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" href="enableCourse.php?courseId={$c.courseId}&amp;rand={$rand}"><img alt="Enable" src="img/icons/unlock.png"/></a>
    {/if}
    {if in_array('delete', $c.actions)}
      <a onclick="return verifyCourseAction('delete', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" href="courseDelete.php?courseId={$c.courseId}&amp;rand={$rand}"><img alt="Delete Course" src="img/icons/delete.png"/></a>
    {/if}
    {if in_array('reports', $c.actions)}
      <a href="reports.php?courseId={$course.courseId}"><img src="img/icons/magnifying_glass.png" alt="View Course Reports" /></a>
    {/if}
    {if in_array('drop', $c.actions)}
      <a href="courseDrop.php?courseId={$c.courseId}&amp;rand={$rand}" onclick="return verifyCourseAction('drop', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" >Drop</a> |
    {/if}
    {if in_array('enroll', $c.actions)}
      <a href="courseEnroll.php?courseId={$c.courseId}&amp;rand={$rand}" onclick="return verifyCourseAction('enroll in', '{$c.section}', '{$c.name}', '{$c.profName}', '{$c.semester}');" >Enroll</a>
    {/if}
<!--    <a href="course_email.php?courseId={$c.courseId}"><img src="img/icons/email.png" alt="EMail" /></a> -->

  </td>
</tr>
{/foreach}

</table>
{elseif $resultMsg|default:true}
<h5 class="center">No Results Found</h5>
{/if}

{$pages|default:''}
