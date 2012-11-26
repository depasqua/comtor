<?php

require_once("loginCheck.php");

// Get user id
$userId = $_SESSION['userId'];

if (!isset($_GET['type']))
{
  header("Location: requests.php");
  exit;
}

// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");

// Check for errors
$error = array();

// Determine request type
switch ($_GET['type'])
{
  case 'acctType':
    // Check that request id is set
    if (!isset($_GET['req_id']))
      $error[] = 'Request id not set.';
    else
    {
      // Determine user id so that we can check that request is for this user
      $userId = null;
      if ($_SESSION['acctType'] != "admin")
        $userId = $_SESSION['userId'];

      switch (deleteAcctChangeRequest($_GET['req_id'], $userId))
      {
        case 0:
          break;
        case 1:
        case 2:
          $error[] = 'Unable to remove request.';
          break;
        case 3:
          $error[] = 'Error removing request from database.';
          break;
        default:
          $error[] = 'Error removing request from database.';
      }
    }

    break;
  case 'acctRemove':
    // Check that user id is set
    if (!isset($_GET['userId']))
      $error[] = 'User id not set.';
    else
    {
      if ($_SESSION['acctType'] != "admin" && $_GET['userId'] != $_SESSION['userId'])
        $error[] = 'Unauthorized to delete request.';
      else
      {
        switch (deleteAcctRemovalRequest($_GET['userId']))
        {
          case 0:
            break;
          case 1:
            $error[] = 'Unable to remove request.';
            break;
          case 2:
            $error[] = 'Error removing request from database.';
            break;
          default:
           $error[] = 'Error removing request from database.';
        }
      }
    }

    break;
}

// Check that there were no errors
if (empty($error))
  $_SESSION['content']['success'] = "Your request has been deleted from the database.";
// Add errors to SESSION to be printed on next page
else
  $_SESSION['content']['error'] = implode('<br/>', $error);

// Assign breadcrumbs
$_SESSION['breadcrumbs'] = array();
$_SESSION['breadcrumbs'][] = array('text' => 'Home', 'href' => 'index.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Requests', 'href' => 'requests.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Delete Request', 'href' => 'request_delete.php');

// Assign tooltips
// $tooltips = file_get_contents('tooltips/test.html');
// $tpl->assign('tooltips', $tooltips);

// Redirect to general output template
header('Location: submission.php');
exit;

?>
