<?php

/*******************************************************************************
* Outputs user info as provided by userInfo.  Expects userInfo to be an
* associative array with keys 'name', 'email', and 'school'
*******************************************************************************/
function displayUserInfo($userInfo)
{
  echo "<h3>{$userInfo['name']}</h3>";
  echo "<h6>E-Mail Address:</h6>{$userInfo['email']}<br/>";
  echo "<h6>School:</h6>{$userInfo['school']}<br/>";
}

/*******************************************************************************
* Outputs course info as provided by courseInfo.  Expects courseInfo to be an
* associative array with keys 'section', 'name', 'profName' (If
* $_SESSION['acctType'] != 'professor'), and 'semester'
*******************************************************************************/
function displayCourseInfo($courseInfo)
{
  echo "<h3>{$courseInfo['section']}: {$courseInfo['name']}</h3>";
  if ($_SESSION['acctType'] != "professor")
    echo "<h6>Professor:</h6>{$courseInfo['profName']}<br/>";
  echo "<h6>Semester:</h6>{$courseInfo['semester']}<br/>";
}

/*******************************************************************************
* Outputs usage of each doclet.  If userId is set, it only counts usage for
* the given user.  If courseId is set, it only counts usage for the given
* course.  If neither are set, it counts all doclet usages.
*******************************************************************************/
function displayDocletUsage($userId = false, $courseId = false)
{
  /* Display use of each doclet */
  if ($doclets = getDoclets())
  {
    echo "<h3>Doclet Usage</h3>\n";
    foreach ($doclets as $doclet)
    {
      // Display name of report
      echo "<h6>{$doclet['docletName']}:</h6> ";

      // Calculate and display number of times the report was run
      /*
      if (($_SESSION['acctType'] == "professor" || $_SESSION['acctType'] == "admin") && ($userId == $_SESSION['userId'] || userId === false))
        $numRows = getDocletRuns($doclet['docletId'], false, ($courseId !== false) ? $courseId : false);
      else
        $numRows = getDocletRuns($doclet['docletId'], $userId, ($courseId !== false) ? $courseId : false);
      */
      $numRows = getDocletRuns($doclet['docletId'], $userId, $courseId);
      echo "selected " . $numRows . " times<br/>\n";
    }
  }
}

/*******************************************************************************
* Outputs links to other pages in lists of results
* $limitLower - lower limit for current page of results
* $limitTotal - total number of results on the current page
* $total - total number of results
* $lowerURL - URL parameter name for lower limit (default: "lower")
* $totalURL - URL parameter name for total of the limit (default: "total")
* $extraURLParams - Extra URL parameters to attach to href's
*******************************************************************************/
function listPages($limitLower, $limitTotal, $total, $lowerURL = "lower", $totalURL = "total", $extraURLParams = "")
{
  if ($extraURLParams != "")
    $extraURLParams = "&amp;".$extraURLParams;

  // Are there others
  if (($limitLower != 0) || ($limitLower + $limitTotal < $total))
  {
    echo "<div class='center'>\n";

    $before = false;  // Flag to indicate if there are any before

    // Are there more before
    if ($limitLower != 0)
    {
      $before = true;
      $newLower = $limitLower - $limitTotal;
      if ($newLower < 0)
        $newLower = 0;
      echo "<a class='prev' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>Previous</a>\n";
    }

    // Determine number of pages that can be displayed before
    $pagesBefore  = $limitLower / $limitTotal;
    $pagesBefore = (int)ceil($pagesBefore);

    // Determine number of pages that can be displayed after
    $pagesAfter  = ($total-($limitLower+$limitTotal)) / $limitTotal;
    $pagesAfter = (int)ceil($pagesAfter);

    // Show 10 or less of previous pages
    for ($i = -1*(($pagesBefore < 10) ? $pagesBefore : 10); $i < 0; $i++)
    {
      $newLower = $limitLower + $i*$limitTotal;
      if ($newLower < 0)
        $newLower = 0;
      $pageNum = $pagesBefore + 1 + $i;
      echo "<a class='pageNum' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>{$pageNum}</a>\n";
    }

    $curPageNum = $pagesBefore + 1;
    echo "<span class='currentPage'>{$curPageNum}</span>\n";

    // Show 10 or less of next pages
    for ($i = 0; $i < (($pagesAfter < 10) ? $pagesAfter : 10); $i++)
    {
      $newLower = $limitLower + ($i+1)*$limitTotal;
      if ($newLower < 0)
        $newLower = 0;
      $pageNum = $pagesBefore + 2 + $i;
      echo "<a class='pageNum' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>{$pageNum}</a>\n";
    }


    // Are there more after
    if ($limitLower + $limitTotal < $total)
    {
      // If both Previous and Next are output, leave space between them
      if ($before)
        echo "<span style='margin-left: 10px;'>\n";

      $newLower = $limitLower + $limitTotal;
      echo "<a class='next' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>Next</a>\n";

      // If both Previous and Next are output, close <span>
      if ($before)
        echo "</span>\n";
    }

    echo "</div>\n";
  }
}

?>
