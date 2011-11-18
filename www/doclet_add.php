<?php

// IMPORTANT: If you allow editing of an existing doclet, you must make sure
// that any assignments that used the default values are updated so that they
// still use the previous default value.  This involves inserting rows into
// the database for each assignment.

$acctTypes = "admin";
require_once("loginCheck.php");

// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");

if (!empty($_POST))
{
  $fatal = false;

  // Ensure that the security random number is correct
  $secure = false;
  require_once("securityFunctions.php");
  if (isset($_POST['securityRand']) && isset($_POST['securityPage']))
    if (checkSecurity($_POST['securityPage'], $_POST['securityRand']) == 1)
      $secure = true;

  if($secure)
  {
    // Check for errors
    $error = array();

    // Check name field for problems
    if (!isset($_POST['name']) || $_POST['name'] == "")
    {
      $error[] = "Doclet name not entered.";
      $fatal = true;
    }
    else if (strlen($_POST['name']) > 255)
    {
      $error[] = "Doclet name too long, truncating.";
      $_POST['name'] = substr($_POST['name'], 0, 255);
    }

    // Check description field for problems
    if (!isset($_POST['desc']) || $_POST['desc'] == "")
    {
      $error[] = "Doclet description not entered.";
      $fatal = true;
    }
    else if (strlen($_POST['desc']) > 255)
    {
      $error[] = "Doclet description too long, truncating.";
      $_POST['desc'] = substr($_POST['desc'], 0, 255);
    }

    // Check javaName field for problems
    if (!isset($_POST['javaName']) || $_POST['javaName'] == "")
    {
      $error[] = "Java class name not entered.";
      $fatal = true;
    }
    else if (strlen($_POST['javaName']) > 255)
    {
      $error[] = "Java class name too long";
      $fatal = true;
    }

    // Remove any html codes from posts
    $_POST['name'] = strip_tags($_POST['name']);
    $_POST['desc'] = strip_tags($_POST['desc']);
    $_POST['javaName'] = strip_tags($_POST['javaName']);

    // Add slashes to all fields if needed
    if (!get_magic_quotes_gpc())
    {
      $_POST['name'] = addslashes($_POST['name']);
      $_POST['desc'] = addslashes($_POST['desc']);
      $_POST['javaName'] = addslashes($_POST['javaName']);
    }

    // Assign errors
    if (!empty($error))
      $_SESSION['msg']['error'] = implode('<br/>', $error);
    else
      $_SESSION['msg']['error'] = "";

    // Proceed if no fatal errors
    if (!$fatal)
    {
      // Insert doclet into database
      switch (addDoclet($_POST['name'], $_POST['desc'], $_POST['javaName'], $docletId))
      {
        case 0:
          $_SESSION['msg']['success'] = 'Doclet successfully added.';
          break;
        default:
          $_SESSION['msg']['error'] .= 'There was an error adding the doclet.';
          break;
      }

      // Add grading options
      if (is_numeric($docletId))
      {
        // Add each section grade
        if (isset($_POST['sectionName']) && isset($_POST['sectionDesc']) && isset($_POST['sectionDefault']))
        {
          $error = "";
          if (count($_POST['sectionName']) != count($_POST['sectionDesc']) || count($_POST['sectionName']) != count($_POST['sectionDefault']))
            $error = "Error adding sections.<br/>";
          else
          {
            foreach ($_POST['sectionName'] as $idx=>$name)
            {
              // Last one will be blank
              $valid = true;
              if ($idx != count($_POST['sectionName']))
              {
                if (empty($name) || empty($_POST['sectionDesc'][$idx]) || $_POST['sectionDefault'][$idx] === null)
                {
                  // Ignore completely blank rows
                  if (!empty($name) || !empty($_POST['sectionDesc'][$idx]) || !empty($_POST['sectionDefault'][$idx]))
                    $error = "Failed to add section(s).  Section details must be filled in.<br/>";
                  $valid = false;
                }
              }
              else
                $valid = false;
              if ($valid && !addDocletSection($docletId, $name, $_POST['sectionDesc'][$idx], $_POST['sectionDefault'][$idx]))
                $error = "Failed to add section(s).<br/>";
            }
          }
          if (!empty($error))
            $_SESSION['msg']['error'] .= $error;
        }

        // Add each param grade
        if (isset($_POST['paramName']) && isset($_POST['paramDesc']) && isset($_POST['paramValue']))
        {
          $error = "";
          if (count($_POST['paramName']) != count($_POST['paramDesc']) || count($_POST['paramName']) != count($_POST['paramValue']))
            $error = "Error adding params.<br/>";
          else
          {
            foreach ($_POST['paramName'] as $idx=>$name)
            {
              // Last one will be blank
              $valid = true;
              if ($idx != count($_POST['paramName']))
              {
                if (empty($name) || empty($_POST['paramDesc'][$idx]) || $_POST['paramValue'][$idx] === null)
                {
                  // Ignore completely blank rows
                  if (!empty($name) || !empty($_POST['paramDesc'][$idx]) || !empty($_POST['paramValue'][$idx]))
                    $error = "Failed to add param(s).  Param details must be filled in.<br/>";
                  $valid = false;
                }
              }
              else
                $valid = false;
              if ($valid && !addDocletParam($docletId, $name, $_POST['paramDesc'][$idx], $_POST['paramValue'][$idx]))
                $error = "Failed to add param(s).<br/>";
            }
          }
          if (!empty($error))
            $_SESSION['msg']['error'] .= $error;
        }
      }
    }

    // Remove the error message if empty
    if ($_SESSION['msg']['error'] == "")
      unset($_SESSION['msg']['error']);
  }

  // Security information incorrect
  else
    $_SESSION['msg']['error'] = 'Security Error.';

  header("Location: index.php");
  exit;
}

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// Assign information if editing
$tpl->assign('editing', $editing);
if ($editing)
{
  $tpl->assign('name', $assignment['name']);

  // Open and close times
  $tpl->assign('openTime', $assignment['openTime']);
  $tpl->assign('closeTime', $assignment['closeTime']);

  // Get and assign current reporting options
  if ($options = getAssignmentOptions($assignmentId))
    $tpl->assign('assignmentOptions', $options);

  $submissions = numAssignmentSubmissions($_GET['assignment_id']);
  $tpl->assign("gradeInputsEnabled", ($submissions == 0));

}

// Get report types (doclets) from database
$params = array(
  'GetGradeSections'=>true,
  'GetGradingParams'=>true,
);

// Get security fields
require_once("securityFunctions.php");
$tpl->assign(securityFormInputs());

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$breadcrumbs[] = array('text' => 'Add Doclet', 'href' => 'doclet_add.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Register function to determine input values
$tpl->register_function('gradeSectionInputValue', 'gradeSectionInputValue');
$tpl->register_function('gradeParameterInputValue', 'gradeParameterInputValue');

// Fetch template
$tpldata = $tpl->fetch('doclet_add.tpl');
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

function gradeSectionInputValue($params, &$tpl)
{
  if(empty($params['docletGradeSectionId']))
    return "";
  else
  {
    global $gradeSections;
    return $gradeSections[$params['docletGradeSectionId']];
  }
}

function gradeParameterInputValue($params, &$tpl)
{
  if(empty($params['docletGradeParameterId']))
    return "";
  else
  {
    global $gradingParams;
    return $gradingParams[$params['docletGradeParameterId']];
  }
}

?>
