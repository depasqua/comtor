<?php
function headFunction()
{
?>
<script type="text/javascript">
function verify() {
var themessage = "You are required to complete the following fields: ";
if (document.form.email.value=="") {
themessage = themessage + " - Email";
}
if (document.form.password.value=="") {
themessage = themessage + " -  Password";
}
//alert if fields are empty and cancel form submit
if (themessage == "You are required to complete the following fields: ") {}
else {
alert(themessage);
return false;
}
}
</script>
<?php
}
?>

<?php include_once("header.php"); ?>

<form action="login.php" method="post" name="form">
<table id="frame">
 <tr>
  <td>Email:</td>
 <tr>
  <td><input type="text" name="email"></td>
 <tr>
  <td>Password:</td>
 <tr>
  <td><input type="password" name="password"></td>
 <tr>
  <td><input type="submit" name="submit" value="Login" onClick="return verify();"></td>
</table>
</form>

<?php include_once("footer.php"); ?>
