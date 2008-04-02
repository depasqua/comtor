<?php require_once("loginCheck.php"); ?>
<?php
  // Buffer output until later so we can redirect if needed
  ob_start();
?>
<?php include_once("header.php"); ?>

<?php
//connect to database
require("connect.php");

// Check for admin permissions, else use current user id
if ((isset($_GET['userId'])) && is_numeric($_GET['userId']) && ($_SESSION['acctType'] == "admin"))
{
  $userId = $_GET['userId'];
}
else
{
  $userId = $_SESSION['userID'];
}

/* Display current user information */
if (($userInfo = getUserInfoById($userId)) !== false)
{
  // Stop buffering output
  ob_end_flush();

  // Print info
  echo "<h1>System Usage</h1>\n";
  echo "<h6>Name:</h6>{$userInfo['name']}<br/>";
  echo "<h6>E-Mail Address:</h6>{$userInfo['email']}<br/>";
  echo "<h6>School:</h6>{$userInfo['school']}<br/>";
}
else
{
  // Clear buffered output
  ob_end_clean();

  // Redirect with error message
  $_SESSION['msg']['error'] = "Specified user does not exist!";
  require_once("redirect.php");
}


/* Display last login, password change, submission, etc. */
echo "<h1>Account History</h1>\n";

$dateFormat = "l F j, Y g:i:s A";

// Get account creation date
$date = strtotime($userInfo['dateTime']);
$date = date($dateFormat, $date);
echo "<h6>Account created:</h6>{$date}<br/>";

// Get account validation date
$date = strtotime($userInfo['validatedDT']);
$date = date($dateFormat, $date);
echo "<h6>Account Validated:</h6>{$date}<br/>";

// Get account last password change date
$date = strtotime($userInfo['passwordChangeDT']);
$date = date($dateFormat, $date);
echo "<h6>Password last changed:</h6>{$date}<br/>";

// Get account last login date
$date = strtotime($userInfo['lastLogin']);
$date = date($dateFormat, $date);
echo "<h6>Last login:</h6>{$date}<br/>";




/* Display use of each doclet */
if ($doclets = getDoclets())
{
  echo "<h1>Doclet Usage</h1>\n";
  foreach ($doclets as $doclet)
  {
    // Display name of report
    echo "<h6>{$doclet['reportName']}:</h6> ";

    // Calculate and display number of times the report was run
    $numRows = getDocletRuns($doclet['reportID'], $userId);
    echo "selected " . $numRows . " times<br/>\n";
  }
}

?>

<?php include_once("footer.php"); ?>
