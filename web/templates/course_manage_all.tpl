{literal}
<script type="text/javascript">
<!--
function verifyCourseAction(action, section, name, professor, semester)
{
  return confirm("Are you sure you want to " + action + " the following course:\nSection: " + section + "\nName: " + name + "\nProfessor: " + professor + "\nSemester: " + semester);
}

function getEnrollToken(elem)
{
  elem.href += "&token=" + prompt("Please enter the enrollment token.  If you do not have one, please ask your professor.");
}

//-->
</script>
{/literal}

<h1>Courses</h1>

{if isset($courses_all)}
  {include file="course_list.tpl" courses_all=$courses_all pages=$pages_all resultMsg=isset($courses_all)}
{/if}
{if isset($courses_enabled)}
  {include file="course_list.tpl" courses_all=$courses_enabled pages=$pages_enabled resultMsg=$courses_enabled group_title='Enabled'}
{/if}
{if isset($courses_disabled)}
  {include file="course_list.tpl" courses_all=$courses_disabled pages=$pages_disabled group_title='Disabled'}
{/if}
