<?php
function headFunction()
{
?>
<script type="text/javascript">
function verify() {
var themessage = "You are required to complete the following fields: ";
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
  <td align="center">Enter your email address and your password will be mailed to you.</td>
 </tr>
</table>

<br>

<form action="recoverPassword.php" method="post" name="form">
<table id="frame">
 <tr>
  <td>Email:</td>
 <tr>
  <td><input type="text" name="email"></td>
 <tr>
  <td><input type="submit" name="submit" value="Submit" onClick="return verify();"></td>
</table>
</form>

<?php include_once("footer.php"); ?>
