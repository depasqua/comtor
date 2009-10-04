<h1>Directory Setup</h1>

<?php require_once("error_box.php"); ?>

<p>
Indicate the paths to the directories where the following items will be stored.
</p>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>

  <fieldset>
    <legend>Directory Setup</legend>
    <div class="floatContainer">
      <label for="www">Web Files: </label>
      <input type="text" id="www" name="www" value="<?php echo !empty($_POST['www']) ? $_POST['www'] : "../../www"; ?>" />
    </div>
    
    <div class="floatContainer">
      <label for="private">Config Files: </label>
      <input type="text" id="private" name="private" value="<?php echo !empty($_POST['private']) ? $_POST['private'] : "../../comtor_config"; ?>" />
    </div>
    
    <div class="floatContainer">
      <label for="code">Java Code: </label>
      <input type="text" id="code" name="code" value="<?php echo !empty($_POST['code']) ? $_POST['code'] : "../../comtor_code"; ?>" />
    </div>
    
    <div class="floatContainer">
      <label for="uploads">File Uploads: </label>
      <input type="text" id="uploads" name="uploads" value="<?php echo !empty($_POST['uploads']) ? $_POST['uploads'] : "../../comtor_uploads"; ?>" />
    </div>

    <div class="floatContainer">
      <label for="resources">Resources: </label>
      <input type="text" id="resources" name="resources" value="<?php echo !empty($_POST['resources']) ? $_POST['resources'] : "../../comtor_resources"; ?>" />
    </div>

  </fieldset>
  
  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
