<?php

$acctTypes = "admin";
require_once("loginCheck.php");

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
      switch (acceptAcctChangeRequest($_GET['req_id']))
      {
        case 0:
          break;
        case 1:
          $error[] = 'Unable to accept request.';
          break;
        case 2:
          $error[] = 'Database error accepting request.';
          break;
        case 3:
          $error[] = 'Request was accepted but there was an error modifying the status.';
          break;
        default:
          $error[] = 'Error accepting request.';
      }
    }

    break;
  case 'acctRemove':
    // Check that user id is set
    if (!isset($_GET['userId']))
      $error[] = 'User id not set.';
    else
    {
      switch (acceptAcctRemovalRequest($_GET['userId']))
      {
        case 0:
          break;
        case 1:
          $error[] = 'Unable to accept request.';
          break;
        case 2:
          $error[] = 'Database error accepting request.';
          break;
        case 3:
          $error[] = 'Request was accepted but there was an error removing the request from the database.';
          break;
        default:
          $error[] = 'Error accepting request.';
      }
    }

    break;
}

// Check that there were no errors
if (empty($error))
  $_SESSION['content']['success'] = "Request has been accepted.";
// Add errors to SESSION to be printed on next page
else
  $_SESSION['content']['error'] = implode('<br/>', $error);

// Assign breadcrumbs
$_SESSION['breadcrumbs'] = array();
$_SESSION['breadcrumbs'][] = array('text' => 'COMTOR', 'href' => 'index.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Requests', 'href' => 'requests.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Delete Request', 'href' => 'request_delete.php');

// Redirect to general output template
header('Location: submission.php');
exit;

?>
