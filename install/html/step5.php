<h1>Relocking Configuration Files</h1>

<?php require_once("error_box.php"); ?>

<p>
It is now safe to relock the files you previous unlocked. To do this, please navigate to the main COMTOR directory via a terminal, and run the following:
</p>

<code>
cd install<br>
bash permissions.sh lock<br>
</code>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>
  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
