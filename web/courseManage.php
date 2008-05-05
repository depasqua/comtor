<?php
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");
?>

<?php include_once("header.php"); ?>

<h1>Course Management</h1>

<?php

  // Connect to database
  include("connect.php");

  // Are we showing a particular course or all courses
  $oneCourse = false;

  if (isset($_GET['courseId']) && is_numeric($_GET['courseId']))
  {
    // Get course info
    $course = getCourseInfo($_GET['courseId']);

    // Check that the id is for the given professor
    if ($course && ($course['profId'] == $_SESSION['userId'] || $_SESSION['acctType'] == "admin"))
    {
      $oneCourse = true;

      /* Display course info if any */
      if (($courseInfo = getCourseInfo($_GET['courseId'])) !== false)
      {
        echo "<h4>{$courseInfo['section']}: {$courseInfo['name']}</h4>\n";

        // Determine professor name
        if ($_SESSION['acctType'] != "professor")
        {
          $courseInfo['profName'] = getUserNameById($courseInfo['profId']);
          echo "<h6>Professor:</h6>{$courseInfo['profName']}<br/>";
        }

        echo "<h6>Semester:</h6>{$courseInfo['semester']}<br/>";
      }

      // Link to view all reports in general category
      echo "<div class='center gapBelowSmall'>\n";
      echo "  <a href='reports.php?courseId={$_GET['courseId']}'><img src='img/icons/magnifying_glass.gif' alt='View All Reports' /></a> View All Course Reports\n";
      echo "</div>\n";

      // Get each student in the course
      if (($students = getCourseStudents($_GET['courseId'])) !== false)
      {
        // Output table header
        echo "<table class='data'>\n";
        echo "<tr>\n";
        echo "  <th>Name</th>\n";
        echo "  <th>E-mail Address</th>\n";
        echo "  <th class='small'>Submissions</th>\n";
        echo "  <th class='small'>Grade</th>\n";
        echo "  <th class='large'>Actions</th>\n";
        echo "</tr>\n";

        foreach ($students as $student)
        {
          // Get array of student reports
          $reports = getUserReports($student['userId'], $_GET['courseId']);
          $submissions = ($reports !== false) ? count($reports) : 0;

          echo "<tr>\n";
          echo "<td class='left'>{$student['name']}</td>\n";
          echo "<td class='left'>{$student['email']}</td>\n";
          echo "<td class='right small'>{$submissions}</td>\n";
          echo "<td class='right small'>N/A</td>\n";

          // Actions
          echo "  <td>\n";
          echo "    <a href='reports.php?userId={$student['userId']}&amp;courseId={$_GET['courseId']}'><img src='img/icons/magnifying_glass.gif' alt='View Reports' /></a>\n";
          echo "</td>\n";


          echo "</tr>\n";
        }

        echo "</table>\n";
      }
      else
      {
        echo "<div class='center'>No students enrolled in this course.</div>\n";
      }
    }
  }


  // Otherwise show all courses
  if (!$oneCourse)
  {
    $courses = getProfCourses($_SESSION['userId']);

    // Show the courses if there are any
    if ($courses !== false && count($courses) > 0)
    {
      // Print table header
      echo"<table class='data'>\n";
      echo"  <tr>\n";
      echo"    <th>Course Section</th>\n";
      echo"    <th>Course Name</th>\n";
      echo"    <th>Semester</th>\n";
      echo"    <th class='large'>Comments</th>\n";
      echo"    <th class='large'>Actions</th>\n";
      echo"  </tr>\n";

      foreach ($courses as $course)
      {
        echo "<tr>\n";
        echo "<td>{$course['section']}</td>\n";
        echo "<td>{$course['name']}</td>\n";
        echo "<td>{$course['semester']}</td>\n";
        echo "<td class='large'>{$course['comment']}</td>\n";

        // Actions
        echo "<td>\n";
        echo "  <a class='course' href='courseManage.php?courseId={$course['courseId']}'><img src='img/icons/magnifying_glass.gif' alt='View Course Details' /></a>\n";
        echo "  <a href='courseEditForm.php?courseId={$course['courseId']}'><img src='img/icons/edit.gif' alt='Edit Course' /></a>\n";
        echo "  <a href='disableCourse.php?courseId={$course['courseId']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"disable\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/lock.gif' alt='Disable' /></a>\n";
        echo "  <a href='enableCourse.php?courseId={$course['courseId']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyCourseAction(\"enable\", \"{$course['section']}\", \"{$course['name']}\", \"{$name}\", \"{$course['semester']}\");' ><img src='img/icons/unlock.gif' alt='Enable' /></a>\n";
        echo "</td>\n";

        echo "</tr>\n";
      }

      // Close the table
      echo "</table>\n";
    }
  }

  // Close database connection
  mysql_close();
?>

<?php include_once("footer.php"); ?>
