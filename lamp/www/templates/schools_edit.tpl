{literal}
<script type="text/javascript">
function verify()
{
  var themessage = "You are required to complete the following fields: ";
  if (document.form.school.value=="")
    themessage = themessage + "\n - School Name";
    
  // Alert if fields are empty and cancel form submit
  if (themessage != "You are required to complete the following fields: ") 
  {
    alert(themessage);
    return false;
  }
}
</script>
{/literal}

<h1>{if $state == "new"}Add{else}Edit{/if} School</h1>

<form action="?state={$state}&amp;schId={$smarty.get.schId}" method="post" name="form">
<table id="frame">
 <tr>
  <td>School Name:</td>
  <td><input type="text" name="school" value="{$school}" /></td>
 </tr>
</table>

<div class="center">
  <input type="submit" name="submit" value="{if $state == "new"}Add{else}Edit{/if} School" onClick="return verify();">
</div>
</form>
