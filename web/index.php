<?
session_start();
//check for session id
if(!isset($_SESSION['userID']))
{
  header("Location: http://csjava.tcnj.edu/~sigwart4/loginForm.php");
  exit;
}
?>

<?php include_once("header.php"); ?>

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

  <div class='gapBelowSmall'>
    Submit for course:
    <select name="course">
      <option value=""></option>
      <?php
        $courses = getCourses($_SESSION['userID']);
        foreach ($courses as $course)
          echo "<option value='{$course['id']}'>{$course['section']}: {$course['name']}</option>\n";
      ?>
    </select>
  </div>

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
      $displayName = $doclet['reportName'];
      $realName = $doclet['javaName'];
      $description = $doclet['reportDescription'];

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

<?php include_once("footer.php"); ?>
