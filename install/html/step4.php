<h1>Write Config Files</h1>

<?php require_once("error_box.php"); ?>

<p>
After submitting, error messages may appear indicating that the permissions are not correct on some files and directories.  If any of these files or directories do not exist, please create them.  Then change the permissions on the files so that the web server can read and write to them.  After this step is over, you may change the permissions to read only on the configuration files.  However, you may not change the permissions on the "uploads" or "template_c" directories.
</p>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>


  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
