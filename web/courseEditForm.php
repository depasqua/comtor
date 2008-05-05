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
<?php

include("connect.php");

/* Get information on the course to be edited and check permissions*/
if (isset($_GET['courseId']))
{
  // Get course info which also checks that the course id is valid
  $courseInfo = getCourseInfo($_GET['courseId'], true);
  if ($courseInfo === false)
  {
    $_SESSION['msg']['error'] = "Invalid course specified.";
    header("Location: courses.php");
    exit;
  }

  // Check that this is this professors course if user is professor
  if ($_SESSION['acctType'] == "professor" && $_SESSION['userId'] != $courseInfo['profId'])
  {
    $_SESSION['msg']['error'] = "You cannot edit any course other than you own.";
    header("Location: courses.php");
    exit;
  }
}
// Indicate error and redirect
else
{
  $_SESSION['msg']['error'] = "Course to be edited was not specified.";
  header("Location: courses.php");
  exit;
}

// Split semester of course into season and year
$arr = explode(" ", $courseInfo['semester']);
$courseInfo['season'] = $arr[0];
$courseInfo['year'] = $arr[1];

?>
<?php include_once("header.php"); ?>

<h1>Edit Course</h1>

<form name='courseForm' method="post" action="courseEdit.php">
<div class='center'>
  <div>
    <span class='formLabel'>Course Section:</span>
    <input type='text' name='section' value='<?php echo $courseInfo['section']; ?>' size='20' maxlength='20'/>
  </div>

  <div>
    <span class='formLabel'>Course Name:</span>
    <input type='text' name='name' value='<?php echo $courseInfo['name']; ?>' size='50' maxlength='255'/>
  </div>

  <!-- Course professor -->
  <?php
    include ("connect.php");
    if ($_SESSION['acctType'] == "admin")
    {
      echo "<div>\n";
      echo "<span class='formLabel'>Professor:</span>\n";
      // Display all professors
      if (($profs = getUsers(array("userId", "name"), "enabled", "professor")) !== false)
      {
        echo "<select name='professor'>\n";

        // Output each professor
        foreach ($profs as $prof)
        {
          echo "<option value='{$prof['userId']}'";
          // Add selected to the current professor
          if ($prof['userId'] == $courseInfo['profId'])
            echo " selected='selected'";
          echo ">{$prof['name']}</option>\n";
        }

        echo "</select>\n";
      }
      echo "</div>\n";
    }
  ?>

  <div>
    <!-- Course semester -->
    <span class='formLabel'>Course Semester:</span>
    <select name='semester'>
      <option <?php if ($courseInfo['season'] == "Fall") echo "selected='selected'"; ?> value='Fall'>Fall</option>
      <option <?php if ($courseInfo['season'] == "Winter") echo "selected='selected'"; ?> value='Winter'>Winter</option>
      <option <?php if ($courseInfo['season'] == "Spring") echo "selected='selected'"; ?> value='Spring'>Spring</option>
      <option <?php if ($courseInfo['season'] == "Summer") echo "selected='selected'"; ?> value='Summer'>Summer</option>
    </select>

    <!-- Course year -->
    <span class='formLabel'>Year:</span>
    <?php
      // Display options for year (1 year prior, 3 years later)
      echo "<select name='year'>\n";
      $year = (int)(date("Y"));
      for ($i = -1; $i < 4; $i++)
      {
        if ($year + $i == $courseInfo['year'])
          echo "<option selected='selected' value='" . ($year + $i) . "'>" . ($year + $i) . "</option>\n";
        else
          echo "<option value='" . ($year + $i) . "'>" . ($year + $i) . "</option>\n";
      }
      echo "</select>\n";
    ?>
  </div>
  <br />

  <!-- Course comments -->
  <div class='courseComments' style='width: 250px; margin: 0px auto; text-align: left;'>
    <span class='formLabel'>Comments:</span>
    <br />
    <textarea name='comment' style='width: 100%; height: 75px;'><?php echo $courseInfo['comment']; ?></textarea>
  </div>

  <?php

    // Output hidden field of course id
    echo "<input type='hidden' name='courseId' value='{$_GET['courseId']}'/>\n";

    // Output security fields
    require_once("securityFunctions.php");
    securityFormInputs();
  ?>
  <input type='submit' class='submit' onClick='return verify();' value='Submit'/>
</div>
</form>

<?php include_once("footer.php"); ?>
