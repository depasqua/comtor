<?php
// Check for session id
session_start();
if(!isset($_SESSION['userID']))
{
  include("redirect.php");
}

// Check for admin permissions if requested
if (isset($adminOnly) && $adminOnly)
  if($_SESSION['acctType'] != "admin")
    include("redirect.php");
?>
