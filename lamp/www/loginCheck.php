<?php
DEFINE("PERMISSION_ERROR", "You do not have permissions to view this page!");

// Check for session id
session_start();

// Check that userId and acctType are set
if(!isset($_SESSION['userId']) || !isset($_SESSION['acctType']))
{
  $_SESSION['msg']['error'] = PERMISSION_ERROR;
  require_once("redirect.php");
}

// If acctTypes is initialized to an array, check that the account type is one of those in the array
if (isset($acctTypes) && is_array($acctTypes))
{
  // Is the user account type one of those specified in acctTypes?
  $valid = false;

  // Try to find the account type in the list
  for ($i = 0; !$valid && $i < count($acctTypes); $i++)
    if ($acctTypes[$i] == $_SESSION['acctType'])
      $valid = true;

  // Redirect invalid users
  if (!$valid)
  {
    $_SESSION['msg']['error'] = PERMISSION_ERROR;
    require_once("redirect.php");
  }
}
// If acctTypes is initialized to a string, check that the account type is the one specified
else if(isset($acctTypes) && is_string($acctTypes))
{
  if ($acctTypes != $_SESSION['acctType'])
  {
    $_SESSION['msg']['error'] = PERMISSION_ERROR;
    require_once("redirect.php");
  }
}
// Otherwise, any account type is accepted

?>
