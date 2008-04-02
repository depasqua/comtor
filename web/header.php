<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<link rel="icon" type="image/gif" href="favicon.gif" />
<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="favicon.ico" />

<link rel="stylesheet" type="text/css" href="css/tables.css" />
<link rel="stylesheet" type="text/css" href="css/layout.css" />
<link rel="stylesheet" type="text/css" media="print" href="css/print.css" />

<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<!-- TemplateBeginEditable name="doctitle" -->
<title>Comment Mentor</title>
<!-- TemplateEndEditable -->
<!-- TemplateBeginEditable name="head" -->

<?php
// Output content of headFunction() if it is defined
if (function_exists("headFunction"))
  headFunction();
?>

<!-- TemplateEndEditable -->
</head>

<body>
<img alt='COMTOR Logo' src='img/logo.gif' />

<div class='header_bar'>
  <div class='bar_left'></div>
  <div class='bar_right'></div>
  <div class='bar_mid_frame'>
    <div class='bar_mid'>
      <div class='username'>
        <?php
          if (isset($_SESSION['username']))
          {
            echo $_SESSION['username'];
            echo "<a href='logout.php'>(Logout)</a>\n";
          }
        ?>
      </div>
      <div class='course_select'>
        <?php
          require_once("connect.php");
          if (isset($_SESSION['username']))
          {
            if ($userCourses = getCourses($_SESSION['userID']))
            {
              echo "<select name='course'>\n";
              echo "<option value=''>Course Select</option>\n";
              foreach ($userCourses as $course)
                echo "<option value='{$course['id']}'>{$course['semester']}: {$course['section']}</option>\n";
            }
            echo "</select>\n";
          }
        ?>
      </div>
    </div>
  </div>
</div>

<!-- Sidebar -->
<div class='sidebar'>

  <!-- Modules -->
  <div class='sidelinks'>
    <div class='mod_top'></div>
    <div class='side_mid'>
      <div class='side_mid_content'>
        <ul>
          <?
            // Random string added to important links for security
            $md5Rand = md5(session_id());

            if(isset($_SESSION['userID']) && ($_SESSION['userID'] != ""))
            {
              echo "<li><a href='usage.php'>System Usage</a></li>\n";
              echo "<li><a href='manageAccounts.php'>Account Management</a></li>\n";
              echo "<li><a href='changePasswordForm.php'>Change Password</a></li>\n";
              echo "<li><a href='disableAccount.php?id={$_SESSION['userID']}&amp;rand={$md5Rand}' onclick='return confirm(\"Are you sure you want to disable your account?\");'>Disable Account</a></li>\n";
              echo "<li><a href='reports.php'>View Report</a></li>\n";
              echo "<li><a href='courses.php'>Courses</a></li>\n";
            }
            else
            {
              echo "<li><a href='registerForm.php'>Create An Account</a></li>\n";
              echo "<li><a href='recoverPasswordForm.php'>Password Recovery</a></li>\n";
            }

            if (isset($_SESSION['acctType']))
            {
              if($_SESSION['acctType']=='admin')
              {
                echo "<li><a href='adminReports.php'>Admin Reports</a></li>\n";
              }

              if($_SESSION['acctType']=='professor' || $_SESSION['acctType']=='admin')
              {
                echo "<li><a href='courseManage.php'>Course Management</a></li>\n";
                echo "<li><a href='courseAddForm.php'>Add Course</a></li>\n";
              }
            }
          ?>

        </ul>
      </div>
    </div>
    <div class='side_bottom'></div>
  </div>


  <!-- Comtor Links -->
  <div class='sidelinks'>
    <div class='comtor_top'></div>
    <div class='side_mid'>
      <div class='side_mid_content'>
        <ul>
          <li><a href="index.php">Home</a></li>
          <li><a href="features.php">Features We Measure</a></li>
          <li><a href="faq.php">FAQ</a></li>
        </ul>
      </div>
    </div>
    <div class='side_bottom'></div>
  </div>

<!-- End Sidebar -->
</div>

<!-- TemplateBeginEditable name="EditRegion" -->
<div class='main_column'>
  <div class='content_frame'>
    <div class='content'>

<?php
  // Show success message if any
  if (isset($_SESSION['msg']['success']))
  {
    echo "<div class='success'>\n";
    echo $_SESSION['msg']['success'];
    echo "</div>\n";
  }
  // Show error message if any
  if (isset($_SESSION['msg']['error']))
  {
    echo "<div class='error'>\n";
    echo $_SESSION['msg']['error'];
    echo "</div>\n";
  }

  // Remove the messages in the session
  if (isset($_SESSION['msg']))
    unset($_SESSION['msg']);
?>
