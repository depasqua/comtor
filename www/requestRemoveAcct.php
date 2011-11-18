<?php

require_once("loginCheck.php");

// Get user id
$userId = $_SESSION['userId'];

// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");

// Check for errors
$error = array();

// Check that the user id exists (It should but just to be sure)
if (!userIdExists($userId))
  $error[] = 'User not found.';

// Check that there were no errors
if (empty($error))
{
  if (requestAcctRemoval($userId))
    $_SESSION['content']['success'] = "Your requested has been entered into the database.  Please have patience while an administrator processes your request.";
  else
    $error[] = 'There was an error requesting to remove your account.<br/>'.mysql_error();
}

// Add errors to SESSION to be printed on next page
$_SESSION['content']['error'] = implode('<br/>', $error);

// Assign breadcrumbs
$_SESSION['breadcrumbs'] = array();
$_SESSION['breadcrumbs'][] = array('text' => 'Home', 'href' => 'index.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Requests', 'href' => 'requests.php');
$_SESSION['breadcrumbs'][] = array('text' => 'Request Account Type Change', 'href' => 'index.php');

// Redirect to general output template
header('Location: submission.php');
exit;

?>
