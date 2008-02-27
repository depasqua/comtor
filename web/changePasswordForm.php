<?php require_once("loginCheck.php"); ?>
<?php
function headFunction()
{
?>
<script type="text/javascript">
function verify()
{
  var themessage = "You are required to complete the following fields: ";
  if (document.form.oldPassword.value == "")
  {
    themessage = themessage + " - Old Password";
  }
  if (document.form.newPassword.value=="")
  {
    themessage = themessage + " -  New Password";
  }
  if (document.form.confirmPassword.value=="")
  {
    themessage = themessage + " -  Confirm Password";
  }
  //alert if fields are empty and cancel form submit
  if (themessage == "You are required to complete the following fields: ")
  {
    if((document.form.newPassword.value.length < 6) || (document.form.confirmPassword.value.length < 6))
    {
      themessage = "The new password must be a minimum of 6 characters!";
    }
    if(themessage == "You are required to complete the following fields: ")
    {
    }
    else
    {
      alert(themessage);
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
<?php
}
?>

<?php include_once("header.php"); ?>

<form action="changePassword.php" method="post" name="form">
<table id="frame">
 <tr>
  <td>Old Password:</td>
 <tr>
  <td><input type="password" name="oldPassword"></td>
 <tr>
  <td>New Password:</td>
 <tr>
  <td><input type="password" name="newPassword"></td>
 <tr>
  <td>Confirm New Password:</td>
 <tr>
  <td><input type="password" name="confirmPassword"></td>
 <tr>
  <td><input type="submit" name="submit" value="Submit" onClick="return verify();"></td>
</table>
</form>

<?php include_once("footer.php"); ?>
