<h1>E-Mail</h1>

<h3>{$course.section}: {$course.name}</h3>
{if $smarty.session.acctType != "professor" }
  <h6>Professor: </h6>{$course.profName}<br/>
{/if}
<h6>Semester: </h6>{$course.semester}<br/>


<form method="post" action="">
<div class="gapBelowSmall">
  <h4>Recipients</h4>
  <select name="recipients[]" multiple="multiple" size="10">
    <option value="{$course.profId}" {if is_array($recipients) && in_array($course.profId, $recipients)}selected="selected"{/if}>{$course.profName} (Professor)</option>
    {foreach from=$users item="u"}
      <option value="{$u.userId}" {if is_array($recipients) && in_array($u.userId, $recipients)}selected="selected"{/if}>{$u.name}</option>
    {/foreach}
  </select>

  <h4>Subject</h4>
  <input name="subject" value="{$subject}" style="width: 250px;"/>

  <h4>E-Mail</h4>
  <textarea name="contents" cols="50" rows="10">{$contents}</textarea>
</div>
<div class="center">
  <input name="securityRand" value="{$rand}" type="hidden" />
  <input name="securityPage" value="{$page_sec}" type="hidden" />
  <input type="submit" value="Send" />
</div>
</form>
