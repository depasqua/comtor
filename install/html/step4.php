<h1>Configuring</h1>

<?php require_once("error_box.php"); ?>

<p>
Please specify the SMTP server to be used to deliver mail.  Also, enter the URL that will be used to access the website.  Since the web pages are stored in the www directory of the parent folder, this will most likely be the current URL but with "install" replaced with "www".  Alternatively, you may set up Apache to point a URL directly to the www directory.  In this case, enter that URL.  In either case, do not include the "http://" part of the URL. 
</p>

<p>
After submitting, error messages may appear indicating that the permissions are not correct on some files and directories.  If any of these files or directories do not exist, please create them.  Then change the permissions on the files so that the web server can read and write to them.  After this step is over, you may change the permissions to readonly on the configuration files.  However, you may not change the permissions on the "uploads" or "template_c" directories.  
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
