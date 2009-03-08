<?php

require_once("loginCheck.php");

// Get user id
$userId = $_SESSION['userId'];

// Get account type to change to
$acctType = isset($_GET['acctType']) ? $_GET['acctType'] : null;

// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");

// Check for errors
$error = array();

// Check that account type is a valid value
if ($acctType == null || ($acctType != 'student' && $acctType != 'professor' && $acctType != 'admin'))
  $error[] = 'Invalid account type (' . $acctType . ').';

// Check that account type is different than current account type
if ($acctType == $_SESSION['acctType'])
  $error[] = 'You\'re account already is of type ' . $acctType . '.';

$comment = '';
if (isset($_GET['comment']))
  $comment = $_GET['comment'];

// Check that the user id exists (It should but just to be sure)
if (!userIdExists($userId))
  $error[] = 'User not found.';

// Check that there were no errors
if (empty($error))
{
  if (requestAcctChange($userId, $acctType, $comment))
    $_SESSION['content']['success'] = "Your requested has been entered into the database.  Please have patience while an administrator processes your request.";
  else
    $error[] = 'There was an error requesting to change your account type.<br/>'.mysql_error();
}

// Add errors to SESSION to be printed on next page
$_SESSION['content']['error'] = implode('<br/>', $error);

// Assign breadcrumbs
$_SESSION['breadcrumbs'] = array();
$_SESSION['breadcrumbs'][] = array('text' => 'COMTOR', 'href' => 'index.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Requests', 'href' => 'requests.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Request Account Type Change', 'href' => 'index.php');

// Redirect to general output template
header('Location: submission.php');
exit;

?>
