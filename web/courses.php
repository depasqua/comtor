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

  // Get and show all courses
  $courses = getCourses();

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
          if ($userCourses[$i]['id'] == $course['id'])
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
        echo "    <a href='courseEditForm.php?course={$course['id']}'><img src='img/icons/edit.gif' alt='Edit Course' /></a>\n";
        echo "    <a href='reports.php?course={$course['id']}'><img src='img/icons/magnifying_glass.gif' alt='View Reports' /></a>\n";
        echo "    <a href='disableCourse.php?course={$course['id']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"disable\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/lock.gif' alt='Disable' /></a>\n";
        echo "    <a href='enableCourse.php?course={$course['id']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"enable\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/unlock.gif' alt='Enable' /></a>\n";
      }

      if ($_SESSION['acctType'] == "admin")
      {
        echo "    <a href='courseDelete.php?course={$course['id']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"delete\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/delete.gif' alt='Delete Course' /></a>\n";
      }

      // Show enroll/drop if the user is a student
      if ($_SESSION['acctType'] == "student")
      {
        if ($enrolled)
          echo "  <a href='courseDrop.php?id={$course['id']}&amp;rand=" . md5(session_id()) . "''>Drop</a>";
        else
          echo "  <a href='courseEnroll.php?id={$course['id']}&amp;rand=" . md5(session_id()) . "''>Enroll</a>";
      }

      echo "</td>\n";
      echo "</tr>\n";
    }
  }
  mysql_close();

?>
</table>

<?php include_once("footer.php"); ?>
