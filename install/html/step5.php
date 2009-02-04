<h1>Configuring</h1>

<?php require_once("error_box.php"); ?>

<p>
This contains a few remaining configuration option.
</p>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>

  <fieldset>
    <legend>Configuring</legend>
    <div class="floatContainer">
      <label for="smtp">SMTP Server: </label>
      <input type="text" id="smtp" name="smtp" value="<?php echo !empty($_POST['smtp']) ? $_POST['smtp'] : "localhost"; ?>" />
    </div>

    <div class="floatContainer">
      <label for="url">URL: </label>
      http://<input type="text" id="url" name="url" value="<?php echo !empty($_POST['url']) ? $_POST['url'] : "localhost"; ?>" />
    </div>
  </fieldset>

  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
