<?php
function headFunction()
{
?>
<script type="text/javascript">
function verify() {
var themessage = "You are required to complete the following fields: ";
if (document.form.name.value=="") {
themessage = themessage + " - Name";
}
if (document.form.school.value=="") {
themessage = themessage + " -  School";
}
if (document.form.email.value=="") {
themessage = themessage + " -  E-mail";
}
//alert if fields are empty and cancel form submit
if (themessage == "You are required to complete the following fields: ") {
  var x = document.forms[0].email.value;
  var filter  = /^([a-zA-Z0-9_\.\-])+\@(([a-zA-Z0-9\-])+\.)+([a-zA-Z0-9]{2,4})+$/;
  if (filter.test(x));
  else {
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
<?php
}
?>

<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center">To signup for an account, fill out the form below.<br>(all fields are required to register)</td>
 </tr>
</table>

<br>

<form action="register.php" method="post" name="form">
<table id="frame">
 <tr>
  <td>Name:</td>
 <tr>
  <td><input type="text" name="name"></td>
 <tr>
  <td>School:</td>
 <tr>
  <td><input type="text" name="school"></td>
 <tr>
  <td>Email:</td>
 <tr>
  <td><input type="text" name="email"></td>
 <tr>
  <td><input type="submit" name="submit" value="Register" onClick="return verify();"></td>
</table>
</form>

<?php include_once("footer.php"); ?>
