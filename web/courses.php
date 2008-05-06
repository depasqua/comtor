<?php
require_once("loginCheck.php");
?>
<?php
function headFunction()
{
?>
<script type='text/javascript'>
<!--
function verifyCourseAction(action, section, name, professor, semester)
{
  return confirm("Are you sure you want to " + action + " the following course:\nSection: " + section + "\nName: " + name + "\nProfessor: " + professor + "\nSemester: " + semester);
}

//-->
</script>
<?php
}
?>
<?php include_once("header.php"); ?>

<?php

//Define the default and maximum number of courses to be shown
define("DEFAULT_LIST_TOTAL", 25);
define("MAX_LIST_TOTAL", 150);

?>

<h1>Courses</h1>

<table class='data'>
<tr>
  <th>Professor</th>
  <th>Course Section</th>
  <th>Course Name</th>
  <th>Semester</th>
  <th class='large'>Comments</th>
  <th class='large'>Actions</th>
</tr>

<?php
  // Connect to database
  include("connect.php");

  // Check if there is a limit set.
  if (!isset($_GET['lower']) || !is_numeric($_GET['lower']))
    $_GET['lower'] = 0;
  if (!isset($_GET['total']) || !is_numeric($_GET['total']))
    $_GET['total'] = DEFAULT_LIST_TOTAL;
  // Check that total is less than the maximum valid
  else if ($_GET['total'] > MAX_LIST_TOTAL)
    $_GET['total'] = MAX_LIST_TOTAL;

  // Get and show all courses
  $courses = getCourses(false, $_GET['lower'], $_GET['total']);

  if ($courses !== false)
  {
    foreach ($courses as $course)
    {
      echo "<tr>\n";

      // Get professors name
      $name = "User #{$course['profId']}";  // Default name
      if ($prof = getUserInfoById($course['profId'], array("name")))
        $name = $prof['name'];

      // Determine if the user is already enrolled in the course
      if ($_SESSION['acctType'] == "student")
      {
        $enrolled = false;
        // Uses $userCourses defined in header.php
        for ($i = 0; $i < count($userCourses) && !$enrolled; $i++)
          if ($userCourses[$i]['courseId'] == $course['courseId'])
            $enrolled = true;
      }

      // Output info
      echo "  <td>{$name}</td>";
      echo "  <td>{$course['section']}</td>";
      echo "  <td>{$course['name']}</td>";
      echo "  <td>{$course['semester']}</td>";
      echo "  <td class='large'>" . nl2br($course['comment']) . "</td>";

      // Actions
      echo "  <td>\n";
      if ($_SESSION['acctType'] == "professor" || $_SESSION['acctType'] == "admin")
      {
        echo "    <a href='courseEditForm.php?courseId={$course['courseId']}'><img src='img/icons/edit.gif' alt='Edit Course' /></a>\n";
        echo "    <a href='courseManage.php?courseId={$course['courseId']}'><img src='img/icons/magnifying_glass.gif' alt='View Course' /></a>\n";
        echo "    <a href='disableCourse.php?courseId={$course['courseId']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"disable\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/lock.gif' alt='Disable' /></a>\n";
        echo "    <a href='enableCourse.php?courseId={$course['courseId']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"enable\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/unlock.gif' alt='Enable' /></a>\n";
      }

      if ($_SESSION['acctType'] == "admin")
      {
        echo "    <a href='courseDelete.php?courseId={$course['courseId']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"delete\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/delete.gif' alt='Delete Course' /></a>\n";
      }

      // Show enroll/drop if the user is a student
      if ($_SESSION['acctType'] == "student")
      {
        if ($enrolled)
        {
          echo "  <a href='reports.php?courseId={$course['courseId']}'><img src='img/icons/magnifying_glass.gif' alt='View Course Reports' /></a>\n";
          echo "  <a href='courseDrop.php?courseId={$course['courseId']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"drop\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");'>Drop</a>";
        }
        else
          echo "  <a href='courseEnroll.php?courseId={$course['courseId']}&amp;rand=" . md5(session_id()) . "'>Enroll</a>";
      }

      echo "</td>\n";
      echo "</tr>\n";
    }
  }
?>
</table>

<?php

  // Add links to view other courses if there are any
  $numCourses = getNumCourses();
  require_once("generalFunctions.php");
  listPages($_GET['lower'], $_GET['total'], $numCourses);

  mysql_close();
?>

<?php include_once("footer.php"); ?>
