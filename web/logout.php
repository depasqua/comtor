<?php
  session_start();

  require_once("connect.php");

  // Update the user in the current users table
  if (isset($_SESSION['userId']))
  {
    // Remove from current users table
    removeCurrentUser($_SESSION['userId']);
  }  
  
  unset($_SESSION);
  session_destroy();

  include("redirect.php");
?>
