<?
$acctTypes = array("professor", "admin");
require_once("loginCheck.php");

$fatal = false;

// Ensure that the security random number is correct
$secure = false;
require_once("securityFunctions.php");
if (isset($_POST['securityRand']) && isset($_POST['securityPage']))
  if (checkSecurity($_POST['securityPage'], $_POST['securityRand']) == 1)
    $secure = true;

if($secure)
{
  // Connect to database
  include("connect.php");

  // Check course name field for problems
  if (!isset($_POST['name']) || $_POST['name'] == "")
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Course name not entered.";
    $fatal = true;
  }
  else if (strlen($_POST['name']) > 255)
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Course name too long, truncating.";
  }

  // Check course section field for problems
  if (!isset($_POST['section']) || $_POST['section'] == "")
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Course section not entered.";
    $fatal = true;
  }
  else if (strlen($_POST['section']) > 20)
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Course section too long, truncating.";
  }

  // Check course semester field for problems
  if (!isset($_POST['semester']) || ($_POST['semester'] != "Fall" && $_POST['semester'] != "Spring" && $_POST['semester'] != "Summer" && $_POST['semester'] != "Winter"))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Course semester invalid.";
    $fatal = true;
  }

  // Check course year field for problems
  if (!isset($_POST['year']) || !is_numeric($_POST['year']))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Course year invalid.";
    $fatal = true;
  }

  $semester = "{$_POST['semester']} {$_POST['year']}";

  // Check if admin and professor is set
  if ($_SESSION['acctType'] == "admin")
  {
    if (!isset($_POST['professor']) || !is_numeric($_POST['professor']))
    {
      if (!isset($_SESSION['msg']['error']))
        $_SESSION['msg']['error'] = "";
      $_SESSION['msg']['error'] .= "Course professor invalid.";
      $fatal = true;
    }
    // Check if id corresponds to a professor
    else
    {
      if (getUserAcctType($_POST['professor']) != "professor")
      {
        if (!isset($_SESSION['msg']['error']))
          $_SESSION['msg']['error'] = "";
        $_SESSION['msg']['error'] .= "Course professor invalid.";
        $fatal = true;
      }
    }
  }

  // Check course year field for problems
  if (!isset($_POST['comment']))
  {
    if (!isset($_SESSION['msg']['error']))
      $_SESSION['msg']['error'] = "";
    $_SESSION['msg']['error'] .= "Course comment not set.";
    $fatal = true;
  }

  // Remove any html codes from posts
  $_POST['name'] = strip_tags($_POST['name']);
  $_POST['section'] = strip_tags($_POST['section']);
  $_POST['comment'] = strip_tags($_POST['comment']);

  // Add slashes to all fields if needed
  if (!get_magic_quotes_gpc())
  {
    $_POST['name'] = addslashes($_POST['name']);
    $_POST['section'] = addslashes($_POST['section']);
    $_POST['comment'] = addslashes($_POST['comment']);
  }

  // Proceed with course registration if no fatal errors
  if (!$fatal)
  {
    // Insert course into database
    $profId = isset($_POST['professor']) ? $_POST['professor'] : $_SESSION['userId'];
    if (addNewCourse($profId, $_POST['name'], $_POST['section'], $semester, $_POST['comment'], $_SESSION['schoolId']))
    {
      $_SESSION['msg']['success'] = "Course successfully added.";
    }
    else
    {
      /*
      if (!isset($_SESSION['msg']['error']))
        $_SESSION['msg']['error'] = "";
      $_SESSION['msg']['error'] .= "MySQL Error: " . mysql_error();
      */
    }
  }

  unset($_SESSION['rand']);
  mysql_close();
}

header("Location: courses.php");
?>
