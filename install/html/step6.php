<h1>Cron Job Setup</h1>

<?php require_once("error_box.php"); ?>

<p>
This sets up the cron job to send E-mail notifications<!-- and perform database backups-->.
</p>

<h6>Please add the following cron job</h6>
<code>0,30 * * * * /usr/bin/php -f <?php echo $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR; ?>cron.php uaeefashdfkjghasdfhrt43a! >> <?php echo $_SESSION["paths"]["private"].DIRECTORY_SEPARATOR; ?>crontab.output</code>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>
  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
