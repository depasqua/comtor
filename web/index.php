<?
session_start();
//check for session id
if(!isset($_SESSION['userId']))
{
  header("Location: http://csjava.tcnj.edu/~sigwart4/loginForm.php");
  exit;
}
?>
<?php include_once("header.php"); ?>

<?php

// Show submit form for students
if ($_SESSION['acctType'] == "student")
{

?>

<form action="run.php" method="post" enctype="multipart/form-data" name="form" onSubmit="return verify()">
<div class='center'>
  <div class='gapBelowSmall'>
    <input type="file" name="file" size="30"/>
    <select name="sourceType">
      <option>Java Source Jar</option>
      <option disabled="disabled">C Source</option>
      <option disabled="disabled">Bash Source</option>
      <option disabled="disabled">Pascal Source</option>
    </select>
  </div>

  <?php
    if (($courses = getCourses($_SESSION['userId'])) !== false)
    {
      echo "<div class='gapBelowSmall'>\n";
      echo "  Submit for course:\n";
      echo "  <select name='courseId'>\n";
      echo "    <option value=''></option>\n";

      // Output each course
      foreach ($courses as $course)
        echo "    <option value='{$course['courseId']}'>{$course['section']}: {$course['name']}</option>\n";

      echo "  </select>\n";
      echo "</div>\n";
    }
  ?>

  <table class="frame center">
  <?php
  // Get list of doclets and descriptions
  // Connect to database
  include("connect.php");

  // Get report types (doclets) from database
  if (($doclets = getDoclets()) !== false)
  {
    // Process results
    foreach ($doclets as $doclet)
    {
      $displayName = $doclet['docletName'];
      $realName = $doclet['javaName'];
      $description = $doclet['docletDescription'];

  ?>
    <tr>
      <td valign="top">
        <input type="checkbox" name="doclet[]" value="<? echo $realName; ?>"/>
      </td>
      <td>
        <div id="doclet"> <? echo $displayName ?> </div><div id="description"> <? echo $description ?> </div>
      </td>
    </tr>
    <?php
    }
  }

  // Close MySQL database
  mysql_close();
  ?>
  </table>


  <input type="submit" name="submit" value="Analyze Comments">
  <input type="Reset" value="Reset"/>
</div>
</form>

<?php

}
// Page for professors and admin
else
{
  echo "<div class='center'>Welcome to COMTOR.  Please use the links to the left to navigate the different pages.</div>\n";
}

?>

<?php include_once("footer.php"); ?>
