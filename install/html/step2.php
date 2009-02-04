<h1>Database Setup</h1>

<?php require_once("error_box.php"); ?>

<p>
Please enter your MySQL username and password.  Please create the database that you specify and grant all privileges on the database to the user specified.
</p>

<form name="form" method="post" action="">
<div>
  <input type="hidden" name="step" value="<?php echo $step; ?>"/>

  <fieldset>
    <legend>MySQL Credentials</legend>
    
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

  <span class="link" onclick="document.form.submit();">Next</span>
</div>
</form>
