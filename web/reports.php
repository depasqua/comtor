<?php require_once("loginCheck.php"); ?>
<?php
  // Block output in case we need to redirect
  ob_start();
?>
<?php require_once("generalFunctions.php"); ?>
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

function verifyUserEventAction(action)
{
  return confirm("Are you sure you want to " + action + " this user event.");
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

// Checks for profId parameter
if ($_SESSION['acctType'] == "admin" && isset($_GET['profId']))
{
  // Check that the id is valid and a professor
  if (getUserAcctType($_GET['profId']) == "professor")
    $profId = $_GET['profId'];
}

// Checks for a course parameter
if (isset($_GET['courseId']) && is_numeric($_GET['courseId']))
{
  // Check that course exists
  if (courseExists($_GET['courseId']))
  {
    // Determine professor name
    if (($courseInfo = getCourseInfo($_GET['courseId'])) !== false)
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
    $courseId = $_GET['courseId'];
  }
  else if ($_SESSION['acctType'] == "professor")
  {
    // Check that the course id is for the given professor
    if ($courseInfo['profId'] == $_SESSION['userId'])
      $courseId = $_GET['courseId'];
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
    if (isUserInCourse($_SESSION['userId'], $_GET['courseId']))
      $courseId = $_GET['courseId'];
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
  $userId = $_SESSION['userId'];

// Stop buffering output
ob_end_flush();

/* Display current user information */
if (($userInfo = getUserInfoById($userId)) !== false)
  displayUserInfo($userInfo);


/* Display course info if any */
if (isset($courseInfo))
  displayCourseInfo($courseInfo);


/* If a report is selected, display report */
if(isset($_GET['userEventId']))
{
  $userEventId = $_GET['userEventId'];

  // Check that userEventId corresponds to this users report
  if (isReportForUser($_GET['userEventId'], $userId) && ($reportInfo = getReportInfo($userEventId)) !== false)
  {
    // Print the date to this report
    $reportDate = $reportInfo['dateTime'];
    $displayDateTime = formatDateTime($reportDate);
    echo "<h3>{$displayDateTime}</h3>\n";


    /* List Files */
    echo "<div id='submittedFiles'>\n";
    echo "<h3>Submitted Files</h3>\n";
    echo "Click a filename to view:<br/>\n";

    if ($files = getReportFiles($userEventId))
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

    if (($doclets = getDoclets()) !== false)
    {
      foreach ($doclets as $doclet)
      {
        $docletId = $doclet['docletId'];
        $name = $doclet['docletName'];
        $description = $doclet['docletDescription'];

        // Find the id that links the userEventId and docletId
        if (($linkId = getSubReportId($docletId, $userEventId)) !== false)
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
}

/* If there is no report selected, show list of user's userEvents */
else
{
  /* Display use of each doclet */
  // Professor - Specific Course
  if ($_SESSION['acctType'] == "professor" && $userId == $_SESSION['userId'] && isset($courseId))
    displayDocletUsage(false, $courseId);

  // Professor - Specific Course and student
  else if ($_SESSION['acctType'] == "professor" && isset($courseId))
    displayDocletUsage($userId, $courseId);

  // Admin
  else if ($_SESSION['acctType'] == "admin" && $userId == $_SESSION['userId'])
    displayDocletUsage(false, isset($courseId) ? $courseId : false);

  else if ($_SESSION['acctType'] == "student")
    displayDocletUsage($userId, isset($courseId) ? $courseId : false);


  /* Display list of userEvents if any */
  if (isset($courseId))
  {
    if (($_SESSION['acctType'] == "professor" || $_SESSION['acctType'] == "admin") && $userId == $_SESSION['userId'])
    {
      $userEvents = getUserReportsByCourse($courseId);
      outputUserEventsTable($userEvents, $courseId, true);
    }
    else
    {
      $userEvents = getUserReports($userId, $courseId);
      outputUserEventsTable($userEvents, $courseId);
    }
  }
  else
  {
    // If professor with no course specified, show reports for all courses
    // If admin and profId is set, show reports for all professor's courses
    if ($_SESSION['acctType'] == "professor" || ($_SESSION['acctType'] == "admin" && isset($profId)))
    {
      if (!isset($profId))
        $profId = $_SESSION['userId'];

      if (($courses = getProfCourses($profId)) !== false)
      {
        foreach ($courses as $course)
        {
          $userEvents = getUserReportsByCourse($course['courseId']);

          if ($userEvents !== false)
          {
            echo "<h3>{$course['section']}: {$course['name']}</h3>\n";

            // Show doclet usage for course
            displayDocletUsage(false, $course['courseId']);

            outputUserEventsTable($userEvents, $course['courseId'], true);
          }
        }
      }
      else
        echo "<div class='center' style='padding: 20px;'><span style='font-weight: bold;'>No courses to display.</span></div>\n";
    }
    // If admin with no course or other user specified, show all reports
    else if ($_SESSION['acctType'] == "admin" && $userId == $_SESSION['userId'])
    {
      // Grouped by course
      if (isset($_GET['grp']) && $_GET['grp'] == "course")
      {
        // Create link for showing all with no grouping
        echo "<div class='center gapBelowSmall'><a href='reports.php'>Show without Groups</a></div>\n";

        // Get a list of all user events so that we can show General Category
        $generalEvents = getUserReports();

        $courses = getCourses();
        if ($courses)
        {
          // Make links at the top that go to each course
          foreach ($courses as $course)
            echo "<a href='#course{$course['courseId']}'>{$course['section']}: {$course['name']}</a><br/>\n";

          foreach ($courses as $course)
          {
            echo "<h3 id='course{$course['courseId']}'>{$course['section']}: {$course['name']}</h3>\n";

            // Get userEvents and display
            if (($userEvents = getUserReportsByCourse($course['courseId'])) !== false && count($userEvents) != 0)
            {
              // Show doclet usage for course
              displayDocletUsage($userId, $course['courseId']);

              // Remove these events from the General Category
              foreach ($userEvents as $event)
                if (($key = array_search($event, $generalEvents)) !== false)
                  unset($generalEvents[$key]);

              outputUserEventsTable($userEvents, false, true);
             }
             else
               outputNoReports();
          }
        }

        // Show the General Category
        if (count($generalEvents) > 0)
        {
          echo "<h3>Other</h3>\n";
          outputUserEventsTable($generalEvents, false, true);
        }
      }
      else
      {
        // Create link for grouping by course
        echo "<div class='center gapBelowSmall'><a href='reports.php?grp=course'>Group by Course</a></div>\n";

        $userEvents = getUserReports();
        outputUserEventsTable($userEvents, false, true);
      }
    }

    // Student - Grouped by course
    else if (isset($_GET['grp']) && $_GET['grp'] == "course")
    {
      // Create link for showing all with no grouping
      echo "<div class='center gapBelowSmall'><a href='reports.php?userId={$userId}'>Show without Groups</a></div>\n";

      // Get a list of all user events so that we can show General Category
      $generalEvents = getUserReports($userId);

      $courses = getCourses($userId);
      if ($courses)
      {
        // Make links at the top that go to each course
        foreach ($courses as $course)
          echo "<a href='#course{$course['courseId']}'>{$course['section']}: {$course['name']}</a><br/>\n";

        foreach ($courses as $course)
        {
          echo "<h3 id='course{$course['courseId']}'>{$course['section']}: {$course['name']}</h3>\n";

          // Get userEvents and display
          if (($userEvents = getUserReports($userId, $course['courseId'])) !== false && count($userEvents) != 0)
          {
            // Show doclet usage for course
            displayDocletUsage($userId, $course['courseId']);

            // Remove these events from the General Category
            foreach ($userEvents as $event)
              if (($key = array_search($event, $generalEvents)) !== false)
                unset($generalEvents[$key]);

            outputUserEventsTable($userEvents);
           }
           else
             outputNoReports();
        }
      }

      // Show the General Category
      if (count($generalEvents) > 0)
      {
        echo "<h3>Other</h3>\n";
        outputUserEventsTable($generalEvents);
      }
    }
    // Student
    else
    {
      // Create link for grouping by course
      echo "<div class='center gapBelowSmall'><a href='reports.php?userId={$userId}&amp;grp=course'>Group by Course</a></div>\n";

      $userEvents = getUserReports($userId);
      outputUserEventsTable($userEvents);
    }
  }
}

// Format date
function formatDateTime($timestamp)
{
  $year=substr($timestamp,0,4);
  $month=substr($timestamp,5,2);
  $day=substr($timestamp,8,2);
  $hour=substr($timestamp,11,2);
  $minute=substr($timestamp,14,2);
  $second=substr($timestamp,17,2);

  //$date = date("M d, Y @ g:i:s A", mktime($hour, $minute, $second, $month, $day, $year));
  $date = date("l F j, Y @ g:i:s A", mktime($hour, $minute, $second, $month, $day, $year));

  return $date;
}

/*******************************************************************************
* Outputs a table of the userEvents.  If courseId is set, this is added to the
* URL parameters of links.  If showUserName is true this adds a column in the
* table to indicate the user who initiated the event
*******************************************************************************/
function outputUserEventsTable($userEvents, $courseId = false, $showUserName = false)
{
  // Check that userEvents is and array
  if ($userEvents !== false && is_array($userEvents))
  {
    echo "<table class='data'>\n";
    echo "  <tr>\n";
    echo "    <th class='mini'>View</th>\n";
    if ($showUserName)
      echo "    <th>User</th>\n";
    echo "    <th>Date</th>\n";
    echo "    <th>Day</th>\n";
    echo "    <th>Time</th>\n";
    // Add delete column for admin
    if ($_SESSION['acctType'] == "admin")
      echo "    <th class='mini'>Delete</th>\n";
    echo "  </tr>\n";

    // Determine courseId and userId part of URL
    $courseURL = $courseId !== false ? "&amp;courseId={$courseId}" : "";

    foreach($userEvents as $userEvent)
    {
      // Format the date and time
      $timestamp = strtotime($userEvent['dateTime']);
      $dayOfWeek = date("l", $timestamp);
      $date = date("F j, Y", $timestamp);
      $time = date("g:i:s A", $timestamp);

      echo "<tr>\n";

      $userURL = "";
      if (isset($userEvent['userId']))
        $userURL = "&amp;userId={$userEvent['userId']}";

      echo "  <td class='mini'><a href=\"reports.php?userEventId={$userEvent['userEventId']}{$courseURL}{$userURL}\"><img src='img/icons/magnifying_glass.gif' alt='View Report' /></a></td>";

      // Show user who initiated the event if needed
      if ($showUserName)
      {
        if (isset($userEvent['userId']) && ($name = getUserNameById($userEvent['userId'])) !== false)
          echo "    <td>{$name}</td>\n";
        else
          echo "    <td>N/A</td>\n";
      }

      echo "  <td>{$date}</td>\n";
      echo "  <td>{$dayOfWeek}</td>\n";
      echo "  <td>{$time}</td>\n";
      // Add delete column for admin
      if ($_SESSION['acctType'] == "admin")
        echo "  <td><a href='userEventDelete.php?userEventId={$userEvent['userEventId']}&amp;rand=" . md5(session_id()) . "' onclick='return verifyUserEventAction(\"delete\");' ><img src='img/icons/delete.gif' alt='Delete Report' /></a></td>\n";
      echo "</tr>\n";
    }
    echo "</table>\n";
  }
  // Print that there are no reports
  else
    outputNoReports();
}

/*******************************************************************************
* Outputs message if no reports are found
*******************************************************************************/
function outputNoReports()
{
  echo "<div class='center' style='padding: 20px;'><span style='font-weight: bold;'>No user reports found.</span></div>\n";
}

?>

<?php include_once("footer.php"); ?>
