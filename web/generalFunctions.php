<?php

/*******************************************************************************
* Outputs usage of each doclet.  If userId is set, it only counts usage for
* the given user.  If courseId is set, it only counts usage for the given
* course.  If neither are set, it counts all doclet usages.
*******************************************************************************/
function getDocletUsage($userId = false, $courseId = false)
{
  /* Display use of each doclet */
  if ($doclets = getDoclets())
  {
    foreach ($doclets as &$doclet)
    {
      // Calculate and display number of times the report was run
      $doclet['numRows'] = getDocletRuns($doclet['docletId'], $userId, $courseId);
    }
  }
  return $doclets;
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
  $rtn = '';

  if ($extraURLParams != "")
    $extraURLParams = "&amp;".$extraURLParams;

  // Are there others
  if (($limitLower != 0) || ($limitLower + $limitTotal < $total))
  {
    $rtn .= "<div class='center'>\n";

    $before = false;  // Flag to indicate if there are any before

    // Are there more before
    if ($limitLower != 0)
    {
      $before = true;
      $newLower = $limitLower - $limitTotal;
      if ($newLower < 0)
        $newLower = 0;
      $rtn .= "<a class='prev' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>Previous</a>\n";
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
      $rtn .= "<a class='pageNum' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>{$pageNum}</a>\n";
    }

    $curPageNum = $pagesBefore + 1;
    $rtn .= "<span class='currentPage'>{$curPageNum}</span>\n";

    // Show 10 or less of next pages
    for ($i = 0; $i < (($pagesAfter < 10) ? $pagesAfter : 10); $i++)
    {
      $newLower = $limitLower + ($i+1)*$limitTotal;
      if ($newLower < 0)
        $newLower = 0;
      $pageNum = $pagesBefore + 2 + $i;
      $rtn .= "<a class='pageNum' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>{$pageNum}</a>\n";
    }


    // Are there more after
    if ($limitLower + $limitTotal < $total)
    {
      // If both Previous and Next are output, leave space between them
      if ($before)
        $rtn .= "<span style='margin-left: 10px;'>\n";

      $newLower = $limitLower + $limitTotal;
      $rtn .= "<a class='next' href='?{$lowerURL}={$newLower}&amp;{$totalURL}={$limitTotal}{$extraURLParams}'>Next</a>\n";

      // If both Previous and Next are output, close <span>
      if ($before)
        $rtn .= "</span>\n";
    }

    $rtn .= "</div>\n";
  }

  return $rtn;
}

/*******************************************************************************
* Sends E-mail to the recipients.
*******************************************************************************/
function sendMail($content, $recipients, $from = null, $subject = null, $bcc = false)
{
  require_once 'Mail.php';

  // Setup E-mail headers
  $headers['From'] = !empty($from) ? $from : EMAIL_FROM;
  if (is_string($recipients))
    $headers[$bcc ? 'Bcc' : 'To'] = $recipients;
  else
    $headers[$bcc ? 'Bcc' : 'To'] = implode(',', $recipients);
  $headers['Subject'] = !empty($subject) ? $subject : '(No Subject)';

  // Add disclaimer if sent from default E-mail address
  if ($headers['From'] == EMAIL_FROM)
    $content .= "<br/><br/><div style=\"text-align: center;\">This is an automated E-mail.  Please do not reply.  If you have any questions, please contact Dr. Depasquale.</div>";

  // Set content type
  $headers['Content-Type'] = 'text/html';

  // Setup mailer object
  $params['host'] = EMAIL_SMTP_HOST;
  $mailer = &Mail::factory('smtp', $params);

  // Send the E-mail
  if (EMAIL_SEND)
    return $mailer->send($headers[$bcc ? 'Bcc' : 'To'], $headers, $content);
  else
    return true;
}

?>
