{literal}
<script type='text/javascript'>
function verify()
{
  // Alert if fields are empty and cancel form submit
  var checked = false;
  for (var i = 0; !checked && i < document.notifyForm.frequency.length; i++)
    if (document.notifyForm.frequency[i].checked)
      checked = true;
  if (!checked)
  {
    message = "You are required to enter notification frequency.";
    alert(message);
    return false;
  }

  // Return true if there were no problems
  return true;
}
</script>
{/literal}

<h1>E-Mail Notifications</h1>

<form name="notifyForm" method="post" action="">
<div>
  <!-- Notification Frequency -->
  <div>
    <h3>Notification Frequency:</h3>
    {foreach from=$notify_frequencies key="id" item="freq"}
      <input type="radio" class="radio" name="frequency" value="{$id}" {if $user_freq == $id}checked="checked"{/if} />{$freq}<br/>
    {/foreach}
  </div>

  <!-- Notification Types -->
  <div>
    <h3>Notification Types:</h3>
    {foreach from=$notify_types key="id" item="type"}
      <input type="checkbox" class="checkbox" name="notify_types[]" value="{$id}" {if $id & $user_notify_types}checked="checked"{/if} /> {$type}<br/>
    {/foreach}
  </div>

  <input name="securityRand" value="{$rand}" type="hidden" />
  <input name="securityPage" value="{$page_sec}" type="hidden" />
  <input class="submit" onclick="return verify();" value="Submit" type="submit">
</div>
</form>
