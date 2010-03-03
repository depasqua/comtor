<h1>COMTOR Source Code Compilation</h1>

<?php require_once("error_box.php"); ?>

<p>
This compiles the COMTOR java source code.<!-- and perform database backups-->.
</p>

<h6>Please ensure you are in the main COMTOR directory and use a command-line to run the following:
</h6>
<code>
cd code<br>
ant deploy
</code>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>
  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
