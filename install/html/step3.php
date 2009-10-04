<h1>Administrator Setup</h1>

<?php require_once("error_box.php"); ?>

<p>
Please enter information for the administrator.  The e-mail address will appear on the website as a contact.  <span style="font-weight: bold;">The default password is "comtor".</span>  You may change the password when you log in.
</p>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>

  <fieldset>
    <legend>Administrator Information</legend>

    <div class="floatContainer">
      <label for="name">Full Name: </label>
      <input type="text" id="name" name="name" value="<?php echo !empty($_POST['name']) ? $_POST['name'] : ""; ?>" />
    </div>
    
    <div class="floatContainer">
      <label for="email">E-mail Address: </label>
      <input type="text" id="email" name="email" value="<?php echo !empty($_POST['email']) ? $_POST['email'] : ""; ?>" />
    </div>

    <div class="floatContainer">
      <label for="school">School Name: </label>
      <input type="text" id="school" name="school" value="<?php echo !empty($_POST['school']) ? $_POST['school'] : ""; ?>" />
    </div>
  </fieldset>

  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
