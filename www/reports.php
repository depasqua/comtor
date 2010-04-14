<?php

require_once("loginCheck.php");

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");
require_once("generalFunctions.php");

//connect to database
include("connect.php");

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'COMTOR', 'href' => 'index.php');

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
    {
      $courseInfo['profName'] = getUserNameById($courseInfo['profId']);
      $breadcrumbs[] = array('text' => $courseInfo['section'].': '.$courseInfo['name'], 'href' => 'courses.php?courseId='.$_GET['courseId']);
    }
  }
  else
  {
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
      $_SESSION['msg']['error'] = "You are not enrolled in this course.";
      header ("Location: courses.php");
      exit;
    }
  }
}

// Checks for user parameter
$userId = false;
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
      $_SESSION['msg']['error'] = "Student not enrolled in this course.";
      header ("Location: courseManage.php");
      exit;
    }
  }
}
if ($userId == false)
  $userId = $_SESSION['userId'];

/* Display current user information */
if (($userInfo = getUserInfoById($userId)) !== false)
  $tpl->assign($userInfo);

/* Display course info if any */
if (isset($courseInfo))
  $tpl->assign('course', $courseInfo);
  
// Determine if the user wants to download the files
$download = isset($_GET['dl']) && $_GET['dl'] = "T";

/* If a report is selected, display report */
if(isset($_GET['userEventId']))
{
  $template = "report.tpl";

  $userEventId = $_GET['userEventId'];

  // Check that userEventId corresponds to this users report
  if (($reportInfo = getReportInfo($userEventId)) !== false)
  {
    // Check that current user can view the report
    if ($reportInfo['userId'] == $userId || $reportInfo['profId'] == $userId || $_SESSION['acctType'] == admin)
    {
      $compileError = $reportInfo['compilationError'];
      unset($reportInfo['compilationError']);
      $tpl->assign($reportInfo);
      
      // Create temporary folder to write the files
      $tmpDir = false;
      if ($download && $tmpDir = tempnam(UPLOAD_DIR, "DL"))
      {
        // tempnam might create a file with the name, so remove it before tring to make the directory
        if (is_file($tmpDir))
          unlink($tmpDir);
        mkdir($tmpDir);
        $codeDir = $tmpDir . DIRECTORY_SEPARATOR . "code";
        mkdir($codeDir);
      }

      if ($files = getReportFiles($userEventId))
      {
        foreach($files as &$file)
        {
          // Write the file to the temp directory if downloading
          if ($download && $tmpDir)
          {
            // Create needed directories
            $path = $codeDir;
            $dirs = explode(DIRECTORY_SEPARATOR, $file['filename']);
            for ($i = 0; $i < count($dirs)-1; $i++)
            {
              $path .= DIRECTORY_SEPARATOR . $dirs[$i];
              if (!is_dir($path))
                mkdir($path);
            }
            
            if (!file_put_contents($codeDir . DIRECTORY_SEPARATOR . $file['filename'], $file['contents']))
            {
            // TODO: Report error
            }            
          }
          // Make the file contents into html encoding
          else
          {
            $file['contents'] = htmlspecialchars($file['contents']);
            $file['contents'] = str_replace(" ", "&nbsp;", $file['contents']);
            $file['contents'] = nl2br($file['contents']);
            $file['contents'] = colorize($file['contents']);
          }
        }
        unset($file);

        if (!$download)
          $tpl->assign('files', $files);
      }

      if ($compileError)
      {
        $output = nl2br(htmlspecialchars($reportInfo['compilationOutput']));
        $tpl->assign("compilationError", $output);
      }
      else if (($doclets = getDoclets()) !== false)
      {
        $score = 0.0;
        $max_score = 0.0;
        foreach ($doclets as &$doclet)
        {
          $docletId = $doclet['docletId'];

          // Find the id that links the userEventId and docletId
          if (($linkId = getSubReportId($docletId, $userEventId)) !== false)
          {
            // Get grading information for this doclet
            getGradingInfo($docletId, $userEventId, $linkId, $doclet['score'], $doclet['max_score']);
            $score += $doclet['score'];
            $max_score += $doclet['max_score'];

            // Get the properties for this doclet
            if (($props = getSubReportProperties($linkId)) !== false)
            {
              $props2 = array();

              // Checks the index to see whether the attribute is a class, method,
              // or comment. Index is from the property list, examples shown below
              foreach($props as $prop)
              {
                $index = $prop['attribute'];
                // property list index - 011
                if(strlen($index) == 3)
                  $props2[] = array('class'=>'class', 'value'=>$prop['value']);
                //property list index - 011.002
                else if(strlen($index) == 7)
                  $props2[] = array('class'=>'method', 'value'=>$prop['value']);
                //property list index - 001.002.a
                else if(strlen($index) > 7){
                  if ($docletId == 1){
                    if (preg_match("#NCORRECT\)$#", $prop['value']))
                      $props2[] = array('class'=>'comment', 'value'=>('<div class=\'incorrect\'>' . $prop['value'] . '</div>'));
                    else
                      $props2[] = array('class'=>'comment', 'value'=>$prop['value']);
                  }
                  else
                    $props2[] = array('class'=>'comment', 'value'=>$prop['value']);
                }
              }

              $doclet['props'] = $props2;
            }
          }
        }
        unset($doclet);
        $tpl->assign('doclets', $doclets);

        // Assign scores
        $tpl->assign("score", $score);
        $tpl->assign("max_score", $max_score);
      }
    }
  }
}

/* If there is no report selected, show list of user's userEvents */
else
{
  $template = "reports.tpl";

  if ($_SESSION['acctType'] == "admin")
    $tpl->assign('deletable', true);

  /* Display use of each doclet */
  // Professor - Specific Course
  if ($_SESSION['acctType'] == "professor" && $userId == $_SESSION['userId'] && isset($courseId))
    $tpl->assign('doclets', getDocletUsage(false, $courseId));

  // Professor - Specific Course and student
  else if ($_SESSION['acctType'] == "professor" && isset($courseId))
    $tpl->assign('doclets', getDocletUsage($userId, $courseId));

  // Admin
  else if ($_SESSION['acctType'] == "admin" && $userId == $_SESSION['userId'])
    $tpl->assign('doclets', getDocletUsage(false, isset($courseId) ? $courseId : false));

  else if ($_SESSION['acctType'] == "student")
    $tpl->assign('doclets', getDocletUsage($userId, isset($courseId) ? $courseId : false));


  /* Display list of userEvents if any */
  if (isset($courseId))
  {
    $showUser = false;
    if (($_SESSION['acctType'] == "professor" || $_SESSION['acctType'] == "admin") && $userId == $_SESSION['userId'])
    {
      $userEvents = getUserReportsByCourse($courseId);
      $showUser = true;
    }
    else
      $userEvents = getUserReports($userId, $courseId);

    if ($userEvents)
    {
      if($showUser)
      {
        foreach ($userEvents as &$ev)
          $ev['user']['name'] = getUserNameById($ev['userId']);
        unset($ev);
      }
    }
    else
      $userEvents = array();

    $tpl->assign('showuser', $showUser);
    $tpl->assign('userEvents', $userEvents);
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
        $groups = array();

        foreach ($courses as $course)
        {
          $grp = array();
          $grp['name'] = true;
          $grp['course'] = $course;

          $userEvents = getUserReportsByCourse($course['courseId']);

          if ($userEvents !== false)
          {
            // Show doclet usage for course
            $grp['doclets'] = getDocletUsage(false, $course['courseId']);

            foreach ($userEvents as &$ev)
              $ev['user']['name'] = getUserNameById($ev['userId']);
            unset($ev);

            $grp['userEvents'] = $userEvents;
          }
          else
            $grp['userEvents'] = array();

          $groups[] = $grp;

          $tpl->assign('showuser', true);
          $tpl->assign('eventGroups', $groups);
        }
      }
    }
    // If admin or student with no course or other user specified, show all reports (for student if student)
    else
    {
      $showUser = false;
      if ($_SESSION['acctType'] == "admin" && $userId == $_SESSION['userId'])
      {
        $userId = false;
        $showUser = true;
      }
      $tpl->assign('showuser', $showUser);

      // Grouped by course
      if (isset($_GET['grp']) && $_GET['grp'] == "course")
      {
        // Get a list of all user events so that we can show General Category
        if (($generalEvents = getUserReports($userId)) !== false)
        {
          $groups = array();
          $courses = getCourses($userId);
          if ($courses)
          {
            foreach ($courses as $course)
            {
              $grp = array();
              $grp['name'] = true;
              $grp['course'] = $course;

              // Show doclet usage for course
              $grp['doclets'] = getDocletUsage($userId, $course['courseId']);

              // Get userEvents and display
              if (($userEvents = getUserReports($userId, $course['courseId'])) !== false && count($userEvents) != 0)
              {
                // Remove these events from the General Category
                foreach ($userEvents as $event)
                  if (($key = array_search($event, $generalEvents)) !== false)
                    unset($generalEvents[$key]);

                // Get user names
                if ($showUser)
                {
                  foreach ($userEvents as &$ev)
                    $ev['user']['name'] = getUserNameById($ev['userId']);
                  unset($ev);
                }

                $grp['userEvents'] = $userEvents;
              }
              else
                $grp['userEvents'] = array();

              $groups[] = $grp;
            }
            unset($course);
          }

          // Show the General Category
          if (count($generalEvents) > 0)
          {
            // Get user names
            if ($showUser)
            {
              foreach ($generalEvents as &$ev)
                $ev['user']['name'] = getUserNameById($ev['userId']);
              unset($ev);
            }

            $grp = array();
            $grp['name'] = 'Other';
            $grp['userEvents'] = $generalEvents;
            $groups[] = $grp;
          }
          $tpl->assign('eventGroups', $groups);
        }
      }
      else
      {
        if (($userEvents = getUserReports($userId)) !== false)
        {
          // Get user names
          if ($showUser)
          {
            foreach ($userEvents as &$ev)
              $ev['user']['name'] = getUserNameById($ev['userId']);
            unset($ev);
          }

          $tpl->assign('userEvents', $userEvents);
        }
      }
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

// Finish Breadcrumbs
$breadcrumbs[] = array('text' => 'View Reports', 'href' => 'reports.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Fetch template
$tpldata = $tpl->fetch($template);
$tpl->assign('tpldata', $tpldata);

// Create jar file and return to user
if ($download && $tmpDir)
{
  // Modify $tpldata
  $tpldata = "<html>
    <head>
      <title>".@$userInfo['name']." Report</title>
      <link rel=\"stylesheet\" type=\"text/css\" href=\"layout.css\" />
    </head>
    <body>{$tpldata}</body>
  </html>";

  $htmlDir = $tmpDir . DIRECTORY_SEPARATOR . "html";  
  if (!mkdir($htmlDir) || !file_put_contents($htmlDir . DIRECTORY_SEPARATOR . "report.html", $tpldata))
  {
    // TODO: Report error
  }
  copy("css/layout.css", $htmlDir . DIRECTORY_SEPARATOR . "layout.css");

  if ($jarName = tempnam($tmpDir, "JAR"))
  {
    // tempnam might create a file with the name, so remove it before tring to make the directory
    if (is_file($jarName))
      unlink($jarName);
    
    // Create jar file
    chdir ($tmpDir);
    exec ("jar -cvf " . $jarName . " *");
      
    // Output jar file
    header('Content-Disposition: attachment; filename="report.jar"');
    echo file_get_contents($jarName);
      
    // Remove the temporary directory
    exec ("rm -rf " . $tmpDir);
  }
  
  exit;
}

// Display template
$tpl->display("htmlmain.tpl");


/******************************************************************************
* Adds colors to java code
* @param string $str String of java program
******************************************************************************/
function colorize($str)
{
  // Make comments green
  $pattern = '/(\/\*.*\*\/)/msU';
  $replacement = '<span style="color: #008000;">$1</span>';
  $str = preg_replace($pattern, $replacement, $str);

  // Make comments green
  $pattern = '/(\/\/.*<br ?\/?>)/i';
  $replacement = '<span style="color: #008000;">$1</span>';
  $str = preg_replace($pattern, $replacement, $str);

  return $str;
}

/******************************************************************************
* Returns whether one string ends with another
* @param string $str String of java program
******************************************************************************/
function endsWith($fullStr, $endStr)
{
  // Get the length of the end string to check for
  $endStrLen = strlen($endStr);
  // Look at the end of $fullStr for a substring the length of endStr
  $fullStrEnd = substr($fullStr, strlen($fullStr) - $endStrLen);
  // Check if they match
  return $fullStrEnd == $endStr;
}
?>
