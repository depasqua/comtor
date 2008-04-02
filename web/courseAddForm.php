<?php
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");
?>
<?php
  function headFunction()
  {
?>
  <script type='text/javascript'>
  function verify()
  {
    var message = "";
    // Determine if course section is set
    if (document.courseForm.section.value == "")
    {
      message += " - Course section\n";
    }
    // Determine if course name is set
    if (document.courseForm.name.value == "")
    {
      message += " - Course name\n";
    }

    // Alert if fields are empty and cancel form submit
    if(message != "")
    {
      message = "You are required to complete the following fields:\n" + message;
      alert(message);
      return false;
    }

    // Return true if there were no problems
    return true;
  }
  </script>
<?php
}
?>

<?php include_once("header.php"); ?>

<h1>Add Course</h1>

<form name='courseForm' method="post" action="courseAdd.php">
<div>
  <span class='formLabel'>Course Section:</span>
  <input type='text' name='section' size='20' maxlength='20'/>

  <br />

  <span class='formLabel'>Course Name:</span>
  <input type='text' name='name' size='50' maxlength='255'/>

  <br />

  <!-- Course professor -->
  <?php
    include ("connect.php");
    if ($_SESSION['acctType'] == "admin")
    {
      echo "<span class='formLabel'>Professor:</span>\n";
      // Display all professors
      if (($profs = getUsers(array("userID", "name"), "enabled", "professor")) !== false)
      {
        echo "<select name='professor'>\n";

        // Output each professor
        foreach ($profs as $prof)
          echo "<option value='{$prof['userID']}'>{$prof['name']}</option>\n";

        echo "</select>\n";
      }
    }
  ?>

  <!-- Course semester -->
  <span class='formLabel'>Course Semester:</span>
  <select name='semester'>
    <option value='Fall'>Fall</option>
    <option value='Winter'>Winter</option>
    <option value='Spring'>Spring</option>
    <option value='Summer'>Summer</option>
  </select>

  <!-- Course year -->
  <span class='formLabel'>Year:</span>
  <?php
    // Display options for year (1 year prior, 3 years later)
    echo "<select name='year'>\n";
    $year = (int)(date("Y"));
    for ($i = -1; $i < 4; $i++)
    {
      if ($i == 0)
        echo "<option selected='selected' value='" . ($year + $i) . "'>" . ($year + $i) . "</option>\n";
      else
        echo "<option value='" . ($year + $i) . "'>" . ($year + $i) . "</option>\n";
    }
    echo "</select>\n";
  ?>

  <br />

  <!-- Course comments -->
  <div class='courseComments' style='width: 250px; margin: 0px auto; text-align: left;'>
    <span class='formLabel'>Comments:</span>
    <br />
    <textarea name='comment' style='width: 100%; height: 75px;'></textarea>
  </div>

  <?php
    // Output security fields
    require_once("securityFunctions.php");
    securityFormInputs();
  ?>
  <input type='submit' class='submit' onClick='return verify();' value='Submit'/>
</div>
</form>

<?php include_once("footer.php"); ?>
