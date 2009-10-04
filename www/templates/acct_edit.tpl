{literal}
<script type="text/javascript">
function verify()
{
  var themessage = "You are required to complete the following fields: ";
  if (document.form.name.value == "")
  {
    themessage = themessage + "\n - Name";
  }
  if (document.form.school.value == "")
  {
    themessage = themessage + "\n -  School";
  }
  if (document.form.email != null && document.form.email.value == "")
  {
    themessage = themessage + "\n -  E-mail";
  }
  // Alert if fields are empty and cancel form submit
  if (themessage == "You are required to complete the following fields: ")
  {
    var x = document.forms[0].email.value;
    var filter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
    if (!filter.test(x))
    {
      alert('Email address is NOT valid!');
      return false;
    }
  }
  else
  {
    alert(themessage);
    return false;
  }
}
</script>
{/literal}

{* Links to other account management pages *}
{if $userId == $smarty.session.userId}
  <a href="changePasswordForm.php">Change Password</a> |
{/if}

{* Change Account Status *}
{if $acctStatus == 'enabled'}
  {assign var="status" value="disable"}
{else}
  {assign var="status" value="enable"}
{/if}
{if $userId == $smarty.session.userId}
  {assign var='q' value='Are you sure you want to '|cat:$status|cat:' your account?'}
{else}
  {assign var='q' value='Are you sure you want to '|cat:$status|cat:' the account for '|cat:$name}
{/if}
<a onclick="return confirm('{$q}');" href="{$status}Account.php?userId={$userId}&amp;rand={$get_rand}">{$status|capitalize} Account</a>

<br/>
<br/>

<form action="editAccount.php?userId={$userId}" method="post" name="form">
<table id="frame">
 <tr>
  <td>Name:</td>
  <td><input type="text" name="name" value="{$name}" /></td>
 </tr>

 <tr>
  <td>School:</td>
  <td>
  <select name ="school">
  {foreach from=$schools item="s"}
    <option value="{$s.schoolId}" {if $s.schoolId == $schoolId}selected="selected"{/if}>{$s.school}</option>
  {/foreach}
  </select>
  </td>
 </tr>

 {if $smarty.session.acctType == 'admin'}
 <tr>
  <td>E-mail:</td>
  <td><input type="text" name="email" value="{$email}" /></td>
 </tr>

 <tr>
  <td>Account Type:</td>
  <td>
    <select name="acctType">
      {foreach from=$acctTypes item="type"}
        <option value="{$type}" {if $type == $acctType}selected="selected"{/if}>{$type|capitalize}</option>
      {/foreach}
    </select>
  </td>
 </tr>
 {/if}
</table>

<div class="center">
  {* Security Fields *}
  <input name="securityRand" value="{$rand}" type="hidden" />
  <input name="securityPage" value="{$page_sec}" type="hidden" />

  <input type="submit" name="submit" value="Update Account" onClick="return verify();" />
</div>
</form>
