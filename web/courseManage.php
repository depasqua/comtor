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

  if (isset($_GET['id']) && is_numeric($_GET['id']))
  {
    // Get course info
    $course = getCourseInfo($_GET['id']);

    // Check that the id is for the given professor
    if ($course && ($course['profId'] == $_SESSION['userID'] || $_SESSION['acctType'] == "admin"))
    {
      $oneCourse = true;

      // Output table header
      echo "<table class='data'>\n";
      echo "<tr>\n";
      //echo "  <th>Id</th>\n";
      echo "  <th>Name</th>\n";
      echo "  <th>E-mail Address</th>\n";
      echo "  <th class='narrow'>Course Submissions</th>\n";
      echo "</tr>\n";

      // Get each student in the course
      $students = getCourseStudents($_GET['id'], true);
      foreach ($students as $student)
      {
        // Get array of student reports
        $reports = getUserReports($student['userID'], $_GET['id']);
        $submissions = ($reports !== false) ? count($reports) : 0;

        echo "<tr>\n";
        echo "<td class='left'>{$student['name']}</td>\n";
        echo "<td class='left'>{$student['email']}</td>\n";
        echo "<td class='right narrow'>{$submissions}</td>\n";
        echo "</tr>\n";
      }

      echo "</table>\n";
    }
  }


  // Otherwise show all courses
  if (!$oneCourse)
  {
    $courses = getProfCourses($_SESSION['userID']);

    // Show the courses if there are any
    if (count($courses))
    {
      // Print table header
      echo"<table class='data'>\n";
      echo"  <tr>\n";
      echo"    <th>Course Section</th>\n";
      echo"    <th>Course Name</th>\n";
      echo"    <th>Semester</th>\n";
      echo"    <th class='large'>Comments</th>\n";
      echo"    <th class='mini'>Actions</th>\n";
      echo"  </tr>\n";

      foreach ($courses as $course)
      {
        echo "<tr>\n";
        echo "<td>{$course['section']}</td>\n";
        echo "<td>{$course['name']}</td>\n";
        echo "<td>{$course['semester']}</td>\n";
        echo "<td class='large'>{$course['comment']}</td>\n";
        echo "<td class='mini'><a class='course' href='courseManage.php?id={$course['id']}'><img src='img/icons/magnifying_glass.gif' alt='View Course Details' /></a></td>\n";
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
