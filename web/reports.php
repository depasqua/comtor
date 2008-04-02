<?php require_once("loginCheck.php"); ?>
<?php
  // Block output in case we need to redirect
  ob_start();
?>
<?php
function headFunction()
{
?>
<script type='text/javascript'>
<!--
function showFile(id)
{
  var element = document.getElementById(id);
  if (element.style.visibility == "visible")
  {
    element.style.visibility = "hidden";
    element.style.display = "none";
  }
  else
  {
    element.style.visibility = "visible";
    element.style.display = "block";
  }
}
//-->
</script>
<?php
}
?>
<?php include_once("header.php"); ?>
<?
//connect to database
include("connect.php");

// Checks for a course parameter
if (isset($_GET['course']) && is_numeric($_GET['course']))
{
  // Check that course exists
  if (courseExists($_GET['course']))
  {
    // Determine professor name
    if (($courseInfo = getCourseInfo($_GET['course'])) !== false)
      $courseInfo['profName'] = getUserNameById($courseInfo['profId']);
  }
  else
  {
    // Clear output buffer, indicate error and redirect
    ob_end_clean();
    $_SESSION['msg']['error'] = "Course does not exist.";
    header ("Location: index.php");
    exit;
  }

  // Set the courseId if user is admin or professor of the course
  if ($_SESSION['acctType'] == "admin")
  {
    $courseId = $_GET['course'];
  }
  else if ($_SESSION['acctType'] == "professor")
  {
    // Check that the course id is for the given professor
    if ($courseInfo['profId'] == $_SESSION['userID'])
      $courseId = $_GET['course'];
    // Indicate error and redirect
    else
    {
      // Clear output buffer
      ob_end_clean();
      $_SESSION['msg']['error'] = "You are not professor for this course.";
      header ("Location: courseManage.php");
      exit;
    }
  }
  // Allow students to filter by course
  else
  {
    // Check if student is in course
    if (isUserInCourse($_SESSION['userID'], $_GET['course']))
      $courseId = $_GET['course'];
    // Indicate error and redirect
    else
    {
      // Clear output buffer
      ob_end_clean();
      $_SESSION['msg']['error'] = "You are not enrolled in this course.";
      header ("Location: courses.php");
      exit;
    }
  }
}

// Checks for user parameter
$userId = -1;
if((isset($_GET['userId'])) && is_numeric($_GET['userId']))
{
  // Set the userId if user is admin or professor of the course the student is in
  if ($_SESSION['acctType'] == "admin")
  {
    $userId = $_GET['userId'];
  }
  else if ($_SESSION['acctType'] == "professor" && isset($courseId))
  {
    // Check that the user id is in the course specified
    if (isUserInCourse($_GET['userId'], $courseId))
      $userId = $_GET['userId'];
    // Indicate error and redirect
    else
    {
      // Clear output buffer
      ob_end_clean();
      $_SESSION['msg']['error'] = "Student not enrolled in this course.";
      header ("Location: courseManage.php");
      exit;
    }
  }
}
if ($userId == -1)
{
  $userId = $_SESSION['userID'];
}

// Stop buffering output
ob_end_flush();

/* Display current user information */
$userInfo = getUserInfoById($userId);
echo "<h6>Name:</h6>{$userInfo['name']}<br/>";
echo "<h6>E-Mail Address:</h6>{$userInfo['email']}<br/>";
echo "<h6>School:</h6>{$userInfo['school']}<br/>";


/* Display course info if any */
if (isset($courseInfo))
{
  echo "<h4>Course Information</h4>\n";
  echo "<h6>Section:</h6>{$courseInfo['section']}<br/>";
  echo "<h6>Name:</h6>{$courseInfo['name']}<br/>";
  echo "<h6>Professor:</h6>{$courseInfo['profName']}<br/>";
  echo "<h6>Semester:</h6>{$courseInfo['semester']}<br/>";
}


/* If a report is selected, display report */
if(isset($_GET['id']))
{
  $masterId = $_GET['id'];

  // Check that masterId corresponds to this users report
  if (isReportForUser($_GET['id'], $userId) && ($reportInfo = getReportInfo($masterId)) !== false)
  {
    // Print the date to this report
    $reportDate = $reportInfo['dateTime'];
    $displayDateTime = formatDateTime($reportDate);
    echo "<h3>{$displayDateTime}</h3>\n";


    /* List Files */
    echo "<div id='submittedFiles'>\n";
    echo "<h4>Submitted Files</h4>\n";
    echo "Click a filename to view:<br/>\n";

    if ($files = getReportFiles($masterId))
    {
      $i = 0;
      foreach($files as $file)
      {
        echo "<a href='javascript:showFile(\"fileContents{$i}\");'>{$file['filename']}</a><br/>\n";

        // Make the file contents into html encoding
        $file['contents'] = htmlspecialchars($file['contents']);
        $file['contents'] = str_replace(" ", "&nbsp;", $file['contents']);
        $file['contents'] = nl2br($file['contents']);
        echo "<code class='javacode' id='fileContents{$i}' style='visibility: hidden; display: none;'>{$file['contents']}</code>\n";
        $i++;
      }
    }
    echo "</div>\n";

    $doclets = getDoclets();
    foreach ($doclets as $doclet)
    {
      $reportId = $doclet['reportID'];
      $name = $doclet['reportName'];
      $description = $doclet['reportDescription'];

      // Find the id that links the masterReportId and docletReportId
      if (($linkId = getSubReportId($reportId, $masterId)) !== false)
      {
        // Get the properties for this doclet
        if (($props = getSubReportProperties($linkId)) !== false)
        {
          echo "<div class='report'>\n";
          echo "<div class='docletDesc'>{$name} ({$description})</div>\n";

          // Checks the index to see whether the attribute is a class, method,
          // or comment. Index is from the property list, examples shown below
          foreach($props as $prop)
          {
            $index = $prop['attribute'];
            // property list index - 011
            if(strlen($index) == 3)
            {
              echo "<hr /><div class='class'>{$prop['value']}</div>\n";
            }
            //property list index - 011.002
            else if(strlen($index) == 7)
            {
              echo "<div class='method'>{$prop['value']}</div>\n";
            }
            //property list index - 001.002.a
            else if(strlen($index) > 7)
            {
              echo "<div class='comment'>{$prop['value']}</div>\n";
            }
          }
          echo "</div>\n";
        }
      }
    }
  }
}

/* If there is no report selected, show list of user's reports */
else
{
  echo "<h3>System Usage</h3>\n";

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

  // Display list of reports if any
  if (isset($courseId))
    $reports = getUserReports($userId, $courseId);
  else
    $reports = getUserReports($userId);
  if ($reports !== false)
  {
    echo "<br/>\n";
    echo "<h3>Reports</h3>\n";
    echo "<table class='data'>\n";
    echo "  <tr>\n";
    echo "    <th class='mini'>View</th>\n";
    echo "    <th>Date</th>\n";
    echo "    <th>Day</th>\n";
    echo "    <th>Time</th>\n";
    echo "  </tr>\n";

    foreach($reports as $row)
    {
      $timestamp = strtotime($row['dateTime']);
      $dayOfWeek = date("l", $timestamp);
      $date = date("F j, Y", $timestamp);
      $time = date("g:i:s A", $timestamp);
      echo "<tr>\n";
      echo "  <td class='mini'><a href=\"reports.php?id={$row['id']}\"><img src='img/icons/magnifying_glass.gif' alt='View Report' /></a></td>";
      echo "  <td>{$date}</td>\n";
      echo "  <td>{$dayOfWeek}</td>\n";
      echo "  <td>{$time}</td>\n";
      echo "</tr>\n";
    }
    echo "</table>\n";
  }
  // Print that there are no reports
  else
  {
    echo "<div class='center' style='padding: 20px;'><span style='font-weight: bold;'>No user reports found.</span></div>\n";
  }
}

//formate date
function formatDateTime($timestamp)
{
    $year=substr($timestamp,0,4);
    $month=substr($timestamp,5,2);
    $day=substr($timestamp,8,2);
    $hour=substr($timestamp,11,2);
    $minute=substr($timestamp,14,2);
    $second=substr($timestamp,17,2);
  $date = date("M d, Y @ g:i:s A", mktime($hour, $minute, $second, $month, $day, $year));
  RETURN ($date);
}
?>

<?php include_once("footer.php"); ?>
