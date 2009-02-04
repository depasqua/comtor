<h1>Cron Job Setup</h1>

<?php require_once("error_box.php"); ?>

<p>
This sets up the cron job to send E-mail notifications and performing database backups.
</p>

<h6>Please add the following cron job</h6>
<code>0,30 * * * * /usr/bin/php -f <?php echo $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR; ?>cron.php uaeefashdfkjghasdfhrt43a! >> <?php echo $_SESSION["paths"]["private"].DIRECTORY_SEPARATOR; ?>crontab.output</code>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>
<!--
  <fieldset>
    <legend>Cron Job</legend>
    <div class="floatContainer">
      <label for="server">Database Server: </label>
      <input type="text" id="server" name="server" value="<?php echo !empty($_POST['server']) ? $_POST['server'] : "localhost"; ?>" />
    </div>

    <div class="floatContainer">
      <label for="username">Username: </label>
      <input type="text" id="username" name="username" value="<?php echo !empty($_POST['username']) ? $_POST['username'] : ""; ?>" />
    </div>

    <div class="floatContainer">
      <label for="password">Password: </label>
      <input type="password" id="password" name="password" value="<?php echo !empty($_POST['password']) ? $_POST['password'] : ""; ?>" />
    </div>

    <div class="floatContainer">
      <label for="dbname">Database Name: </label>
      <input type="text" id="dbname" name="dbname" value="<?php echo !empty($_POST['dbname']) ? $_POST['dbname'] : "comtor"; ?>" />
    </div>
  </fieldset>
-->
  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
