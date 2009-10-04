<?php

header('Content-Type: text/xml');
header("Cache-Control: no-cache, must-revalidate");
header("Expires: Mon, 26 Jul 1997 05:00:00 GMT");

echo '<?xml version="1.0" encoding="ISO-8859-1"?>' . "\n";
echo '<doclets>' . "\n";

require_once("connect.php");
require_once("mysqlFunctions.php");

if (isset($_GET['assignment_id']))
{
  // Get and assign current reporting options
  if ($options = getAssignmentOptions($_GET['assignment_id']))
    foreach ($options as $opt)
      echo '<doclet>' . $opt . '</doclet>' . "\n";
}

echo '</doclets>' . "\n";

?>
