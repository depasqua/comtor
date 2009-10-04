{literal}
<script type="text/javascript">
function verify() {
var themessage = "You are required to complete the following fields: ";
if (document.form.name.value=="")
  themessage = themessage + "\n - Name";
if (document.form.school.value==-1)
  themessage = themessage + "\n - School";
if (document.form.email.value=="")
  themessage = themessage + "\n - E-mail";
if (document.form.prof_req.checked && document.form.prof_link.value=="")
  themessage = themessage + "\n - Link to verify that you are a professor";
//alert if fields are empty and cancel form submit
if (themessage == "You are required to complete the following fields: ")
{
  var x = document.forms[0].email.value;
  var filter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
  if (filter.test(x));
  else
  {
    alert('Email address is NOT valid!');
    return false;
  }
}
else {
alert(themessage);
return false;
}
}
</script>
{/literal}


<table>
 <tr>
  <td align="center">To signup for an account, fill out the form below.<br>(all fields are required to register)</td>
 </tr>
</table>

<br/>

<form action="register.php" method="post" name="form">
<table id="frame">

 <tr>
  <td>Name:</td>
  <td><input type="text" name="name"></td>
 </tr>

 <tr>
  <td>School:</td>
  <td>
  <select name ="school">
    <option value="-1"></option>
  {foreach from=$schools item="s"}
    <option value="{$s.schoolId}" {if $s.schoolId == $schoolId}selected="selected"{/if}>{$s.school}</option>
  {/foreach}
  </select>
  </td>
 </tr>

 <tr>
  <td>Email:</td>
  <td><input type="text" name="email"></td>
 </tr>
</table>

<div class="center">
  {* Request to be Professor *}
  <input type="checkbox" id="prof_req" name="prof_req" />
  <label for="prof_req">Request Professor Status</label>

  <br/>

  <label for="prof_link">Please provide a link to a website that can be used to verify that you are a professor.</label>
  <input type="text" id="prof_link" name="prof_link" style="width: 90%;"/>

  <br/>

  <input type="submit" name="submit" value="Register" onClick="return verify();">
</div>
</form>
