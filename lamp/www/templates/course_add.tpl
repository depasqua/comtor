{literal}
<script type='text/javascript'>
function verify()
{
  var message = "";
  // Determine if course section is set
  if (document.courseForm.section.value == "")
  {
    message += " - Course section\n";
  }
  // Determine if course name is set
  if (document.courseForm.name.value == "")
  {
    message += " - Course name\n";
  }

  // Alert if fields are empty and cancel form submit
  if(message != "")
  {
    message = "You are required to complete the following fields:\n" + message;
    alert(message);
    return false;
  }

  // Return true if there were no problems
  return true;
}
</script>
{/literal}

<h1>{if $edit|default:false}Edit{else}Add{/if} Course</h1>

<form name="courseForm" method="post" action="course{if $edit|default:false}Edit{else}Add{/if}.php">
<div class="center">
  <div>
    <span class="formLabel">Course Section:</span>
    <input name="section" size="20" maxlength="20" type="text" value="{$section}">
  </div>

  <div>
    <span class="formLabel">Course Name:</span>
    <input name="name" size="50" maxlength="255" type="text" value="{$name}">
  </div>

  {if is_array($profs) && count($profs) > 0}
  <!-- Course professor -->
  <div>
    <span class="formLabel">Professor:</span>
    <select name="professor">
    {foreach from=$profs item="p"}
      <option value="{$p.userId}" {if $profId == $p.userId}selected="selected"{/if}>{$p.name}</option>
    {/foreach}
    </select>
  </div>
  {/if}

  <div>
    <!-- Course semester -->
    <span class="formLabel">Course Semester:</span>
    <select name="semester">
      <option value="Fall" {if $semester == 'Fall'}selected="selected"{/if}>Fall</option>
      <option value="Winter" {if $semester == 'Winter'}selected="selected"{/if}>Winter</option>
      <option value="Spring" {if $semester == 'Spring'}selected="selected"{/if}>Spring</option>
      <option value="Summer" {if $semester == 'Summer'}selected="selected"{/if}>Summer</option>
    </select>

    <!-- Course year -->
    <span class="formLabel">Year:</span>
    <select name="year">
      {if !isset($year)}
        {assign var="year" value=$smarty.now|date_format:'%Y'}
      {/if}
      {section name="y" start=$start_year loop=$end_year+1}
        <option value="{$smarty.section.y.index}" {if $year == $smarty.section.y.index}selected="selected"{/if}>{$smarty.section.y.index}</option>
      {/section}
    </select>

  </div>

  <!-- Course comments -->
  <div class="courseComments" style="margin: 0px auto; width: 250px; text-align: left;">
    <span class="formLabel">Comments:</span>
    <br>
    <textarea name="comment" style="width: 100%; height: 75px;">{$comment}</textarea>
  </div>

  {if $edit|default:false}
    <input name="courseId" value="{$courseId}" type="hidden" />
  {/if}
  <input name="securityRand" value="{$rand}" type="hidden" />
  <input name="securityPage" value="{$page_sec}" type="hidden" />
  <input class="submit" onclick="return verify();" value="Submit" type="submit">
</div>
</form>
