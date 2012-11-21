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
      <script type='text/javascript'>
      <!--
      function CourseSelected()
      {
        var url = "reports.php?courseId=" + document.getElementById('courseId').value;
        window.location = url;
      }
      //-->
      </script>
        <?php
          require_once("connect.php");

          if (isset($_SESSION['username']))
          {
            if ($_SESSION['acctType'] == "admin")
              $userCourses = getCourses();
            else if ($_SESSION['acctType'] == "professor")
              $userCourses = getProfCourses($_SESSION['userId']);
            else
              $userCourses = getCourses($_SESSION['userId']);
            if ($userCourses !== false)
            {
              echo "<select id='courseId' name='courseId' onchange='CourseSelected();' >\n";
              echo "<option value=''>Course Select</option>\n";
              foreach ($userCourses as $course)
                echo "<option value='{$course['courseId']}'>{$course['semester']}: {$course['section']}</option>\n";
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


<?php

class Link
{
  var $href;
  var $name;
  var $attrs = array();

  function Link($name, $href)
  {
    $this->name = $name;
    $this->href = $href;
  }

  function addAttr($attr, $value)
  {
    array_push($this->attrs, new Attribute($attr, $value));
  }

  function toString()
  {
    $str = "<a href='{$this->href}'";

    // Output attributes
    foreach ($this->attrs as $attr)
      $str.= " " . $attr->toString();

    $str .= ">{$this->name}</a>";

    return $str;
  }
}

class Attribute
{
  var $attr;
  var $value;

  function Attribute($attr, $value)
  {
    $this->attr = $attr;
    $this->value = $value;
  }

  function toString()
  {
    return "{$this->attr}='{$this->value}'";
  }
}


/* Create array of links to go in the Modules Sidebar */
$moduleLinks = array();

// Random string added to important links for security
$md5Rand = md5(session_id());

// Create array of links that are to be shown for each account type
if (isset($_SESSION['acctType']))
{
  // Submit files or Welcome message
  if ($_SESSION['acctType'] == "student")
    array_push($moduleLinks, new Link("Submit Files", "index.php"));
  else
    array_push($moduleLinks, new Link("Welcome", "index.php"));

  // Add links for all account types
  array_push($moduleLinks, new Link("System Usage", "usage.php"));

  if ($_SESSION['acctType'] == "admin")
    array_push($moduleLinks, new Link("Admin Reports", "adminReports.php"));

  // Add links for all account types
  array_push($moduleLinks, new Link("Account Management", "manageAccounts.php"));
  array_push($moduleLinks, new Link("Change Password", "changePasswordForm.php"));

  // Disable account link
  $link = new Link("Disable Account", "disableAccount.php?userId={$_SESSION['userId']}&amp;rand={$md5Rand}");
  $link->addAttr("onclick", "return confirm(\"Are you sure you want to disable your account?\");");
  array_push($moduleLinks, $link);

  // Determine page for Course Management
  if ($_SESSION['acctType'] == "student")
  {
    array_push($moduleLinks, new Link("Course Management", "courses.php"));
  }
  else if($_SESSION['acctType'] == "professor")
  {
    array_push($moduleLinks, new Link("Course Management", "courseManage.php"));
  }
  else if ($_SESSION['acctType'] == "admin")
  {
    array_push($moduleLinks, new Link("Course Management", "courses.php"));
  }


  // Add Course - Professor and Admin
  if ($_SESSION['acctType'] == "professor" || $_SESSION['acctType'] == "admin")
  {
    array_push($moduleLinks, new Link("Add Course", "courseAddForm.php"));
  }

  // View Reports - All account types
  array_push($moduleLinks, new Link("View Reports", "reports.php"));
}

// Show the Modules Sidebar if there are links that go in it
if (count($moduleLinks) > 0)
{
  echo "<!-- Modules -->\n";
  echo "<div class='sidelinks'>\n";
  echo "  <div class='mod_top'></div>\n";
  echo "  <div class='side_mid'>\n";
  echo "    <div class='side_mid_content'>\n";
  echo "      <ul>\n";

  // Output links
  foreach ($moduleLinks as $link)
    echo "<li>" . $link->toString() . "</li>\n";

  echo "      </ul>\n";
  echo "    </div>\n";
  echo "  </div>\n";
  echo "  <div class='side_bottom'></div>\n";
  echo "</div>\n";
}

?>


  <!-- Comtor Links -->
  <div class='sidelinks'>
    <div class='comtor_top'></div>
    <div class='side_mid'>
      <div class='side_mid_content'>
        <ul>
          <?php
            if (!isset($_SESSION['acctType']))
            {
              echo "<li><a href='loginForm.php'>Login</a></li>\n";
              echo "<li><a href='registerForm.php'>Create An Account</a></li>\n";
              echo "<li><a href='recoverPasswordForm.php'>Password Recovery</a></li>\n";
            }
          ?>
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
