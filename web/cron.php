<?php

define('CRON_GAP', 1800); // Number of seconds until next cronjob

if ($_SERVER['argc'] != 2 || $_SERVER['argv'][1] != 'uaeefashdfkjghasdfhrt43a!')
  die;

// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");
require_once("generalFunctions.php");

$now = time();

// Get time of last cron job
$last = 0;
$query = 'SELECT time FROM cronjobs ORDER BY time DESC LIMIT 1';
$result = mysql_query($query);
if ($result && mysql_num_rows($result) > 0)
  $last = mysql_result($result, 0);

// Insert time of this cronjob
$query = 'INSERT INTO cronjobs (time) VALUES (' . $now . ')';
mysql_query($query);

$format = 'Y-m-d H:i:s';

// Check if an assignment opened since last time
$query = 'SELECT * FROM assignments WHERE openTime > "' . date($format, $last) . '" AND openTime <= "' . date($format, $now) . '"';
$result = mysql_query($query);
if ($result)
{
  while ($row = mysql_fetch_assoc($result))
  {
    $course = getCourseInfo($row['courseId']);
    $content = 'The dropbox has opened for the following assignment: ' . $row['name'] . '<br/>';
    $subject = $course['section'].' - Assignment Dropbox Open';
    setupNotify($course['profId'], NOTIFY_ASSIGNMENT_OPEN, $content, $subject);
    if ($students = getCourseStudents($row['courseId']))
      foreach ($students as $user)
        setupNotify($user['userId'], NOTIFY_ASSIGNMENT_OPEN, $content, $subject);
  }
}

// Check if an assignment closed since last time
$query = 'SELECT * FROM assignments WHERE closeTime > "' . date($format, $last) . '" AND closeTime <= "' . date($format, $now) . '"';
$result = mysql_query($query);
if ($result)
{
  while ($row = mysql_fetch_assoc($result))
  {
    $course = getCourseInfo($row['courseId']);
    $content = 'The dropbox has closed for the following assignment: ' . $row['name'] . '<br/>';
    $subject = $course['section'].' - Assignment Dropbox Closed';
    setupNotify($course['profId'], NOTIFY_ASSIGNMENT_CLOSE, $content, $subject);
    if ($students = getCourseStudents($row['courseId']))
      foreach ($students as $user)
        setupNotify($user['userId'], NOTIFY_ASSIGNMENT_CLOSE, $content, $subject);
  }
}

// Check if an assignment will close in the next 24 hours (Try to do just before midnight.)
if (date('n/j/Y', $now) != date('n/j/Y', $now+CRON_GAP))
{
  $query = 'SELECT * FROM assignments WHERE closeTime > "' . date($format, $now) . '" AND closeTime <= "' . date($format, $now+24*3600+2*CRON_GAP) . '"';
  $result = mysql_query($query);
  if ($result)
  {
    while ($row = mysql_fetch_assoc($result))
    {
      $course = getCourseInfo($row['courseId']);
      $content = 'The dropbox closed for the following assignment in less than 24 hours: ' . $row['name'] . '<br/>';
      $subject = $course['section'].' - Assignment Dropbox Deadline Approaching';
      setupNotify($course['profId'], NOTIFY_ASSIGNMENT_DEADLINE_NEAR, $content, $subject);
      if ($students = getCourseStudents($row['courseId']))
        foreach ($students as $user)
          setupNotify($user['userId'], NOTIFY_ASSIGNMENT_DEADLINE_NEAR, $content, $subject);
    }
  }
}


// Get and send notifications
if ($notifications = getNotifications())
{
  $emails = array();

  foreach ($notifications as $notification)
  {
    $emails[$notification['userId']][] = array($notification['content'], $notification['subject'], $notification['userNotificationId']);
  }

  foreach ($emails as $userId=>$userEmails)
  {
    $content = $userEmails[0][0];
    $subject = $userEmails[0][1];
    $ids = array($userEmails[0][2]);
    if (count($userEmails) > 1)
    {
      $ids = array();
      $content = '';
      $subject = 'Comment Mentor Notifications';

      foreach ($userEmails as $email)
      {
        $content .= '<b>'.$email[1].'</b><br/>'.$email[0].'<br/><br/>';
        $ids[] = $email[2];
      }
    }

    // Get user email address
    if ($address = getUserInfoById($userId, array('email')))
    {
      $address = $address['email'];
      if (sendMail($content, $address, null, $subject))
      {
        // Delete form db
        deleteNotifications($ids);
      }
    }
  }
}

echo date('n/j/Y H:i:s')." - Done!\n";

?>
