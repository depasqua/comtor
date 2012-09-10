<?php

/*******************************************************************************
* Get last report from database for given user
* Returns false if no report is found, otherwise the id of last report.
*******************************************************************************/
function lastReport($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "SELECT userEventId FROM userEvents WHERE userId={$userId} GROUP BY dateTime ORDER BY dateTime desc LIMIT 1";
  $result = mysql_query($query);

  $userEventId = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $userEventId = (int) $row['userEventId'];

  return $userEventId;
}

/*******************************************************************************
* Inserts row into database indicating that the given userEventId was submitted
* for the given courseId.
* Assumes that the caller has already checked that the userEventId and courseId
* are valid for the current user.
* Returns true or false to indicatie if the row was successfully inserted
*******************************************************************************/
function recordReportForCourse($userEventId, $courseId)
{
  // Check that userEventId and courseId are numeric before query
  if (!is_numeric($userEventId) || !is_numeric($courseId))
    return false;

  // Check that the given courseId exists
  if (!courseExists($courseId))
    return false;

  $query = "INSERT INTO courseEvents (userEventId, courseId) VALUES ({$userEventId}, {$courseId})";
  return mysql_query($query);
}

/*******************************************************************************
* Checks if the given userId is in the database
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false.
*******************************************************************************/
function userIdExists($userId)
{
  $result = mysql_query('SELECT userId FROM users WHERE userId='.$userId.' LIMIT 1');

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Checks if there is already an account for the given e-mail address.
* Assumes that the caller has already checked that the parameters are valid.
* If $userId is numeric, ignores the address if the address is for the user id
* Returns true or false.
*******************************************************************************/
function emailExists($email, $userId = null)
{
  $where = "";
  if (is_numeric($userId))
    $where = " AND NOT userId=".$userId;

  $result = mysql_query("SELECT email FROM users WHERE email='$email'{$where} LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Inserts a new user into the database
* Assumes that the caller has already checked that the parameters are valid
* Returns new user id or false to indicate if the user was successfully inserted
*******************************************************************************/
function createNewUser($name, $email, $cryptPassword, $schoolId, $acctType = "student", $enabled = "enabled")
{
  // Check that school id is valid
  if (!schoolExists($schoolId))
    return false;

  $query = "INSERT INTO users(name, email, password, schoolId, acctType, acctStatus) VALUES ('{$name}', '{$email}', '{$cryptPassword}', {$schoolId}, '{$acctType}', '{$enabled}')";
  $rtn = mysql_query($query);

  // Check for error and get auto_increment value if successful
  if ($rtn !== false)
  {
    $tmp = mysql_insert_id();
    if ($tmp !== false)
      $rtn = $tmp;
  }

  return $rtn;
}

/*******************************************************************************
* Updates user info in the database
* Assumes that the caller has already checked that the parameters are valid.
* Null parameters indicate that the value is to remain the same.
* Returns true or false to indicate if the user was successfully updated
*******************************************************************************/
function updateUser($userId, $name = null, $email = null, $cryptPassword = null, $schoolId = null, $acctType = null, $enabled = null)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  // Check that school exists
  if ($schoolId !== null && !schoolExists($schoolId))
    return false;

  $query = 'UPDATE users SET ';

  // Add columns
  $cols = array();
  if ($name !== null)
    $cols[] = "name='{$name}' ";
  if ($email !== null)
    $cols[] = "email='{$email}' ";
  if ($password !== null)
    $cols[] = "password='{$cryptPassword}' ";
  if ($schoolId !== null)
    $cols[] = "schoolId={$schoolId} ";
  if ($acctType !== null)
    $cols[] = "acctType='{$acctType}' ";
  if ($acctStatus !== null)
    $cols[] = "acctStatus='{$enabled}' ";

  // Check that at least one column is set
  if (empty($cols))
    return false;

  $query .= implode(', ', $cols);

  // Indicate user id
  $query .= 'WHERE userId='.$userId;

  return mysql_query($query);
}

/*******************************************************************************
* Gets user info for the given email address.  This info returned is are the
* columns listed in the columns array or * if no columns are provided.
* Assumes that the caller has already checked that the email address is valid
* Returns false if user is not found and an associative array of user info if
* user is found
*******************************************************************************/
function getUserInfoByEmail($email, $columns = array())
{
  // Create column selection text
  if (($select = makeColumnStr($columns, "users")) === false)
    return false;

  $query = "SELECT {$select} FROM users WHERE email='{$email}' LIMIT 1";
  $result = mysql_query($query);

  $user = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $user = $row;

  return $user;
}

/*******************************************************************************
* Gets user info for the given userId.  This info returned is are the columns
* listed in the columns array or * if no columns are provided.
* Assumes that the caller has already checked that the email address is valid
* Returns false if user is not found and an associative array of user info if
* user is found
*******************************************************************************/
function getUserInfoById($userId, $columns = array())
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  // Create column selection text
  if (($select = makeColumnStr($columns, "users")) === false)
    return false;

  $query = "SELECT {$select} FROM users INNER JOIN schools WHERE users.schoolID=schools.schoolID && users.userId={$userId} LIMIT 1";
  $result = mysql_query($query);

  $user = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $user = $row;

  return $user;
}

/*******************************************************************************
* Sets the account validation date (and last login) for the given user to now.
* Returns true or false to indicate if the date was successfully changed
*******************************************************************************/
function setValidationDate($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "UPDATE users SET validatedDT=NOW(), lastLogin=NOW() WHERE userId={$userId}";
  return mysql_query($query);
}

/*******************************************************************************
* Sets the last login date for the given user to now.
* Returns true or false to indicate if the date was successfully changed
*******************************************************************************/
function setLastLoginDate($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "UPDATE users SET lastLogin=NOW() WHERE userId={$userId}";
  return mysql_query($query);
}

/*******************************************************************************
* Gets a specific doclet and info
* Returns false if doclet is not found or an associative array of doclet info
* if doclet is found
*******************************************************************************/
function getDoclet($docletId)
{
  // Check that docletId is numeric before query
  if (!is_numeric($docletId))
    return false;

  $query = "SELECT * FROM doclets WHERE docletId={$docletId}";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  return mysql_fetch_assoc($result);
}

/*******************************************************************************
* Gets all doclets and their info
*
* @param array $params Associative array of parameters
*
* Returns false if no doclets are found and an array of associative arrays of
* doclet info if any doclets are found
*******************************************************************************/
function getDoclets($params = array())
{
  // Default parameters
  $getGradeSections = false;
  $getGradingParams = false;

  // Process parameters
  foreach ($params as $key=>$value)
  {
    switch ($key)
    {
      case 'GetGradeSections':
        $getGradeSections = $value;
        break;
      case 'GetGradingParams':
        $getGradingParams = $value;
        break;
    }
  }

  $query = "SELECT * FROM doclets";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $doclets = array();

  while ($row = mysql_fetch_assoc($result))
  {
    // Get grade sections
    if ($getGradeSections)
    {
      $gs = array();

      $query = "SELECT * FROM docletGradeSections WHERE docletId=".$row['docletId'];
      if (($result2 = mysql_query($query)) !== false)
        while ($row2 = mysql_fetch_assoc($result2))
          $gs[] = $row2;

      $row['gradeSections'] = $gs;
    }

    // Get grading parameters
    if ($getGradingParams)
    {
      $gp = array();

      $query = "SELECT * FROM docletGradeParameters WHERE docletId=".$row['docletId'];
      if (($result2 = mysql_query($query)) !== false)
        while ($row2 = mysql_fetch_assoc($result2))
          $gp[] = $row2;

      $row['gradeParams'] = $gp;
    }

    array_push($doclets, $row);
  }

  return $doclets;
}

/*******************************************************************************
* Gets all properties for the given subReportId
* Returns false if no properties are found and an array of associative arrays of
* property info if any properties are found
*******************************************************************************/
function getSubReportProperties($docletEventId)
{
  // Check that docletEventId is numeric before query
  if (!is_numeric($docletEventId))
    return false;

  $query = "SELECT * FROM docletOutputItems WHERE docletEventId={$docletEventId} ORDER BY attribute";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $props = array();

  while ($row = mysql_fetch_assoc($result))
    array_push($props, $row);

  return $props;
}

/*******************************************************************************
* Checks if a courseId exists
* Returns true or false.
*******************************************************************************/
function courseExists($courseId)
{
  // Check that courseId is numeric before query
  if (!is_numeric($courseId))
    return false;

  $result = mysql_query("SELECT courseId FROM courses WHERE courseId = {$courseId} LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Sets the status of the given course
* Returns true or false to indicate if successful
*******************************************************************************/
function setCourseStatus($courseId, $status)
{
  // Check that courseId is numeric before query
  if (!is_numeric($courseId))
    return false;

  $query = "UPDATE courses SET status='{$status}' WHERE courseId={$courseId}";
  return mysql_query($query);
}

/*******************************************************************************
* Returns total number of courses
*******************************************************************************/
function getNumCourses($where = array(), $status = null)
{
  if ($status != null)
    $where[] = 'status=\'' . $status . '\'';

  $query = 'SELECT COUNT(*) FROM courses';
  if (count($where) > 0)
    $query .= ' WHERE ' . implode(' AND ', $where);

  $result = mysql_query($query);

  // Ensure that result is a valid MySQL resourse
  if (!$result)
    return false;

  return mysql_result($result, 0);
}

/*******************************************************************************
* Gets all course info for the given courseId.  If comment parameter is true,
* the comment field is returned in the array, otherwise it is not.
* Returns false if course is not found and an associative array of course info
* if course is found
*******************************************************************************/
function getCourseInfo($courseId, $comment = false)
{
  // Check that courseId is numeric before query
  if (!is_numeric($courseId))
    return false;

  if ($comment)
    $query = "SELECT * FROM courses WHERE courseId = {$courseId} LIMIT 1";
  else
    $query = "SELECT profId, section, name, semester FROM courses WHERE courseId = {$courseId} LIMIT 1";

  $result = mysql_query($query);

  $course = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $course = $row;

  return $course;
}

/*******************************************************************************
* Gets user name for the given id
* Returns false if user is not found and users name if found
*******************************************************************************/
function getUserNameById($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "SELECT name FROM users WHERE userId = {$userId} LIMIT 1";
  $result = mysql_query($query);

  $name = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $name = $row['name'];

  return $name;
}

/*******************************************************************************
* Check that the given userId is in the given courseId
* Returns true or false
*******************************************************************************/
function isUserInCourse($userId, $courseId)
{
  // Check that userId and courseId are numeric before query
  if (!is_numeric($userId) || !is_numeric($courseId))
    return false;

  $query = "SELECT courseId FROM enrollments WHERE courseId = {$courseId} AND studentId = {$userId} LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Gets a list of all reports for the given userId.  If courseId is sent in as a
* parameter and is not false, only the reports for that courseId are returned.
* If userId is not specified, all reports are shown.
* Returns false if no reports are found. If any are found, returns an array of
* associative arrays (one for each report) with 'userId', 'userEventId' and
* 'dateTime'.
* The array is in order of most recent report to older report.
*******************************************************************************/
function getUserReports($userId = false, $courseId = false)
{
  // Check that userId and courseId (if set) are numeric before query
  if (($userId !== false && !is_numeric($userId)) || ($courseId !== false && !is_numeric($courseId)))
    return false;

  $where = ($userId !== false) ? " WHERE userId={$userId}" : "";

  $query = "SELECT userId, userEventId, dateTime FROM userEvents{$where} ORDER BY dateTime DESC";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // reports found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $reports = array();

  while ($row = mysql_fetch_assoc($result))
  {
    // Check that this is for the specified course (if set).  Uses short circuit
    // evaluation!
    if ($courseId === false || isReportForCourse($row['userEventId'], $courseId) || isReportForCourseAssignment($row['userEventId'], $courseId))
      array_push($reports, $row);
  }

  return $reports;
}

/*******************************************************************************
* Gets a list of all reports for the given courseId.
* Returns false if no reports are found. If any are found, returns an array of
* associative arrays (one for each report) with 'userEventId' and 'dateTime'.  The array
* is is order of most recent report to older report.
*******************************************************************************/
function getUserReportsByCourse($courseId)
{
  // Check that courseId is numeric before query
  if (!is_numeric($courseId))
    return false;


//  $query = "SELECT userEventId FROM courseEvents WHERE courseId = {$courseId}";
  $query = "SELECT userEventId FROM assignmentEvents WHERE assignmentId IN (SELECT assignmentId FROM assignments WHERE courseId={$courseId})";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // reports found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $reports = array();

  while ($row = mysql_fetch_assoc($result))
    if (($userEvent = getReportInfo($row['userEventId'])) !== false)
      array_push($reports, $userEvent);

  return $reports;
}


/*******************************************************************************
* Check if the given userEventId was submitted for the given courseId
* Returns true or false
*******************************************************************************/
function isReportForCourse($userEventId, $courseId)
{
  // Check that userEventId and courseId are numeric before query
  if (!is_numeric($userEventId) || !is_numeric($courseId))
    return false;

  $query = "SELECT userEventId FROM courseEvents WHERE userEventId = {$userEventId} AND courseId = {$courseId} LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Check if the given userEventId was submitted for the given assignmentId
* Returns true or false
*******************************************************************************/
function isReportForAssignment($userEventId, $assignmentId)
{
  // Check that userEventId and assignmentId are numeric before query
  if (!is_numeric($userEventId) || !is_numeric($assignmentId))
    return false;

  $query = "SELECT userEventId FROM assignmentEvents WHERE userEventId = {$userEventId} AND assignmentId={$assignmentId} LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Check if the given userEventId was submitted for the given courses's
* assignments
* Returns true or false
*******************************************************************************/
function isReportForCourseAssignment($userEventId, $courseId)
{
  // Check that userEventId and courseId are numeric before query
  if (!is_numeric($userEventId) || !is_numeric($courseId))
    return false;

  $query = "SELECT userEventId FROM assignmentEvents WHERE userEventId={$userEventId} AND assignmentId IN (SELECT assignmentId FROM assignments WHERE courseId={$courseId}) LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Check if the given userEventId was submitted by the given userId
* Returns true or false
*******************************************************************************/
function isReportForUser($userEventId, $userId)
{
  // Check that userEventId and userId are numeric before query
  if (!is_numeric($userEventId) || !is_numeric($userId))
    return false;

  $query = "SELECT userEventId FROM userEvents WHERE userEventId={$userEventId} AND userId={$userId} LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Gets an associative array of all information the given userEventId.
* Returns false if no files are found. If any are found, returns an array of
* associative arrays (one for each file).
*******************************************************************************/
function getReportInfo($userEventId)
{
  // Check that userEventId is numeric before query
  if (!is_numeric($userEventId))
    return false;

  $query = "SELECT * FROM userEvents WHERE userEventId={$userEventId} LIMIT 1";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // reports found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  return mysql_fetch_assoc($result);
}

/*******************************************************************************
* Gets an array of all file for the given userEventId.
* Returns false if no files are found. If any are found, returns an array of
* associative arrays (one for each file).
*******************************************************************************/
function getReportFiles($userEventId)
{
  // Check that userEvent is numeric before query
  if (!is_numeric($userEventId))
    return false;

  $query = "SELECT filename, contents FROM files WHERE userEventId={$userEventId}";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // reports found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $files = array();

  while ($row = mysql_fetch_assoc($result))
    array_push($files, $row);

  return $files;
}

/*******************************************************************************
* Gets docletEventId identifying a given doclets portion of the given userEventId
* Returns false if no report is found, otherwise the id.
*******************************************************************************/
function getSubReportId($docletId, $userEventId)
{
  // Check that docletId and userEventId are numeric before query
  if (!is_numeric($docletId) || !is_numeric($userEventId))
    return false;

  $query = "SELECT docletEventId FROM docletEvents WHERE userEventId={$userEventId} AND docletId={$docletId} LIMIT 1";
  $result = mysql_query($query);

  $id = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $id = (int) $row['docletEventId'];

  return $id;
}

/*******************************************************************************
* Gets number of times the given doclet was run.  If userId was sent as a
* parameter and is not false, it counts only the reports for the given userId.
* If courseId was sent as a parameter and is not false, it counts only the
* reports for the given courseId.  If both userId and courseId are sent in, it
* counts only reports for the given userId and the given courseId.
* Returns false on error, otherwise the number of time the report was run.
*******************************************************************************/
function getDocletRuns($docletId, $userId = false, $courseId = false)
{
  // Check that docletId, userId (if set), and courseId (if set) are numeric
  // before query
  if (
      !is_numeric($docletId) ||
      ($userId !== false && !is_numeric($userId)) ||
      ($courseId !== false && !is_numeric($courseId))
     )
    return false;

  // Construct the additional info to go in the WHERE clause of query
  $where = "";

  // Find all main reports for the user (Both if course is set or not)
  if ($userId !== false)
  {
    // getUserReports ignores $courseId if it is false
    if (($reports = getUserReports($userId, $courseId)) !== false)
    {
      // Add to WHERE clause using OR, it will later be removed from the first one
      foreach ($reports as $report)
        $where .= "OR userEventId={$report['userEventId']} ";
    }
  }

  // Find all main reports for a course
  else if ($courseId !== false)
  {
    if (($reports = getUserReportsByCourse($courseId)) !== false)
    {
      // Add to WHERE clause using OR, it will later be removed from the first one
      foreach ($reports as $report)
        $where .= "OR userEventId={$report['userEventId']} ";
    }
  }

  // Fix up the where clause
  if ($where != "")
  {
    // This is going after another WHERE condition so add AND
    // Remove first OR and add parenthesis
    $where = " AND (" . substr($where, 3) . ")";
  }
  else if ($userId !== false || $courseId !== false)
  {
    return 0;
  }

  $query = "SELECT COUNT(*) FROM docletEvents WHERE docletId={$docletId}{$where}";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return mysql_result($result, 0);
}

/*******************************************************************************
* Checks if there the given password is correct for the given userId.
* Returns true or false.
*******************************************************************************/
function correctPassword($userId, $password)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $result = mysql_query("SELECT password FROM users WHERE userId=$userId");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  $password = crypt($password, 'cm');

  return (mysql_result($result, 0) == $password);
}

/*******************************************************************************
* Sets the password for the given user to the given password and sets the
* password changed date to now.
* Returns true or false to indicate if the password was successfully changed
*******************************************************************************/
function setPassword($userId, $password)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "UPDATE users SET password='{$password}', passwordChangeDT=NOW() WHERE userId={$userId}";
  return mysql_query($query);
}

/*******************************************************************************
* Sets the password for the given user to the given password and sets the
* password changed date to now.
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the password was successfully changed
*******************************************************************************/
function setPasswordByEmail($email, $password)
{
  $query = "UPDATE users SET password='{$password}', passwordChangeDT=NOW() WHERE email='{$email}'";
  return mysql_query($query);
}

/*******************************************************************************
* Disables the given userId
* Returns true or false to indicate if successful
*******************************************************************************/
function disableUser($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "UPDATE users SET acctStatus='disabled' WHERE userId={$userId}";
  return mysql_query($query);
}

/*******************************************************************************
* Gets all column names for the specified table.
* Assumes that the table name is valid and will not allow SQL injection
* Returns an array of column names or false on failure
*******************************************************************************/
function getColumns($table)
{
  $result = mysql_query("SHOW COLUMNS FROM {$table}");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  $columns = array();

  // Get just the names from each column
  while ($row = mysql_fetch_assoc($result))
  {
    array_push($columns, $row['Field']);
  }

  return $columns;
}

/*******************************************************************************
* Gets number of users.  enabled can be 'enabled', 'disabled', or 'all'.
*******************************************************************************/
function getNumUsers($enabled = "all")
{
  // Create WHERE clause for enabled
  $where = "";
  if ($enabled == "enabled")
    $where = " WHERE acctStatus='enabled'";
  else if ($enabled == "disabled")
    $where = " WHERE acctStatus='disabled'";

  $query = "SELECT COUNT(*) FROM users{$where}";
  $result = mysql_query($query);

  // Check that result is a valid MySQL resource
  if (!$result)
    return false;

  return mysql_result($result, 0);
}

/*******************************************************************************
* Gets all users and the info provided in the fields parameter.
* fields is a array of column names in the database that are to be retrieved.
* lower and total are the limits to be put on the query.
* enabled can be 'enabled', 'disabled', or 'all'.
* $custom_where allows for a custom where clause that will be appended to the
*   automatic where clause
* Returns an array of associative arrays of user info
*******************************************************************************/
function getUsers($fields = array(), $enabled = "all", $acctType = "all", $lower = false, $total = false, $schoolId = null, $custom_where = null)
{
  // Create WHERE clause for enabled
  $where = array();
  if ($enabled == "enabled")
    $where[] = "acctStatus='enabled'";
  else if ($enabled == "disabled")
    $where[] = "acctStatus='disabled'";

  // Create limit string
  if (is_numeric($lower) && is_numeric($total) && $lower >= 0 && $total > 0)
    $limit = " LIMIT {$lower}, {$total}";
  else
    $limit = "";

  if ($acctType != all)
  {
    // Check that the given acctType is a valid column value
    $validValues = getEnumValues("users", "acctType");
    if (in_array($acctType, $validValues))
      $where[] = "acctType='{$acctType}'";
  }

  if ($schoolId !== null)
//    $where[] = "UPPER(school)='" . strtoupper($school) . "'";
    $where[] = 'schoolId =' . $schoolId;

  $where = implode(' AND ', $where);

  // Add custom where
  if ($custom_where !== null)
    if ($where != '')
      $where = "({$where}) AND ({$custom_where})";
    else
      $where = $custom_where;

  if ($where != '')
    $where = ' WHERE ' . $where;

  // Create column selection text
  if (($columns = makeColumnStr($fields, "users")) === false)
    return false;

  $query = "SELECT {$columns}, schools.school FROM users INNER JOIN schools ON (users.schoolID=schools.schoolID) {$where} ORDER BY name{$limit}";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // reports found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $users = array();

  while ($row = mysql_fetch_assoc($result))
    array_push($users, $row);

  return $users;
}

/*******************************************************************************
* Returns a string of columns in format "col1, col2, col3" from the columns
* array but first checks that these fields are in the given table. Defaults to
* "*" if there are no valid columns in the array
* Assumes that the table name is valid and will not allow SQL injection
*******************************************************************************/
function makeColumnStr($columns, $table)
{
  // Create array of acceptable column names
  if (($validColumns = getColumns($table)) === false)
    return false;

  // Create column selection text
  $text = "";
  foreach ($columns as $column)
  {
    // Check that column is a valid column name in the table
    if (in_array($column, $validColumns))
      $text .= "{$column}, ";
  }

  // If text is empty, get all info
  if ($text == "")
    $text = "*";
  // Otherwise, remove the ending ", " from the string
  else
    $text = substr($text, 0, strlen($text) - 2);

  return $text;
}

/*******************************************************************************
* Deletes the given userEvent(s).  Accepts variable number of parameters or an
* array.
* Returns true or false to indicate if the userEvent was successfully deleted
*******************************************************************************/
function deleteUserEvent($userEventId)
{
  $rtn = true;

  /* Get userEvents */
  if (is_array($userEventId))
    $userEvents = $userEventId;
  else
    $userEvents = func_get_args();

  // Create WHERE string for these event ids
  $where = "";
  foreach ($userEvents as $userEventId)
  {
    // Check that userEventId is numeric before query
    if (!is_numeric($userEventId))
      $rtn = false;
    else
      $where .= " OR userEventId=" . $userEventId;
  }

  /* Delete all related data */
  if ($rtn && $where != "")
  {
    // Remove initial OR
    $where = substr($where, 3);

    // Delete from userEvents
    $query = "DELETE FROM userEvents WHERE".$where;
    $rtn = (mysql_query($query) && $rtn);

    // Delete from courseEvents
    $query = "DELETE FROM courseEvents WHERE".$where;
    $rtn = (mysql_query($query) && $rtn);

    // Delete from assignmentEvents
    $query = "DELETE FROM assignmentEvents WHERE".$where;
    $rtn = (mysql_query($query) && $rtn);

    // Delete from files
    $query = "DELETE FROM files WHERE".$where;
    $rtn = (mysql_query($query) && $rtn);

    /* Get docletEventIds */
    $query = "SELECT docletEventId FROM docletEvents WHERE".$where;
    $result = mysql_query($query);
    if ($result && mysql_num_rows($result) > 0)
    {
      // Create WHERE string for these event ids
      $docletEventWhere = "";
      while ($userEvent = mysql_fetch_assoc($result))
        $docletEventWhere .= " OR docletEventId=" . $userEvent['docletEventId'];
      // Remove initial OR
      $docletEventWhere = substr($docletEventWhere, 3);

      // Delete from docletEvents
      $query = "DELETE FROM docletOutputItems WHERE".$docletEventWhere;
      $rtn = (mysql_query($query) && $rtn);
    }

    // Delete from docletEvents
    $query = "DELETE FROM docletEvents WHERE".$where;
    $rtn = (mysql_query($query) && $rtn);
  }

  return $rtn;
}

/*******************************************************************************
* Deletes the account for given userId
* TODO: Delete all other database entries for the courses that are removed for a
* professor
* Returns true or false to indicate if the user was successfully deleted
*******************************************************************************/
function deleteUser($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $rtn = false;
  $acctType = "";

  $query = "SELECT acctType FROM users WHERE userId={$userId} LIMIT 1";
  $result = mysql_query($query);
  if ($result && mysql_num_rows($result) == 1)
  {
    $acctType = mysql_result($result, 0);
    $rtn = true;
  }

  /* Delete all related data */
  if ($rtn)
  {
    // Get userEvents
    if (($userEvents = getUserReports($userId)) !== false)
    {
      $arr = array();
      foreach ($userEvents as $userEvent)
        array_push($arr, $userEvent['userEventId']);

      deleteUserEvent($arr);
    }

    /* Get courses if professor */
    if ($acctType == "professor")
    {
      if (($courses = getProfCourses($userId)) !== false)
      {
        // Create array of these course ids
        $arr = array();
        foreach ($courses as $course)
          array_push($arr, $course['courseId']);

        // Delete courses
        deleteCourse($arr);
      }
    }
    /* Remove from course enrollments if student */
    else if ($acctType == "student")
    {
      // Delete from enrollments
      $query = "DELETE FROM enrollments WHERE studentId=".$userId;
      $rtn = (mysql_query($query) && $rtn);
    }

    $query = "DELETE FROM users WHERE userId={$userId} LIMIT 1";
    $rtn = mysql_query($query);
  }

  return $rtn;
}

/*******************************************************************************
* Enables the account for given userId
* Returns true or false to indicate if the user was successfully enabled
*******************************************************************************/
function enableUser($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "UPDATE users SET acctStatus='enabled' WHERE userId={$userId} LIMIT 1";
  return mysql_query($query);
}

/*******************************************************************************
* Sets the account type for given userId
* Returns true or false to indicate if the user account type was changed
*******************************************************************************/
function setUserAcctType($userId, $acctType)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  // Check that the given acctType is a valid column value
  $validValues = getEnumValues("users", "acctType");
  if (!in_array($acctType, $validValues))
    return false;

  $query = "UPDATE users SET acctType = '{$acctType}' WHERE userId={$userId} LIMIT 1";
  return mysql_query($query);
}

/*******************************************************************************
* Return the account type for given userId or false if the userId was invalid
*******************************************************************************/
function getUserAcctType($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "SELECT acctType FROM users WHERE userId={$userId} LIMIT 1";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  return mysql_result($result, 0);
}

/*******************************************************************************
* Return the possible account types
*******************************************************************************/
function getUserAcctTypes()
{
  return getEnumValues('users', 'acctType');
}

/*******************************************************************************
* Returns an array of possible values for the given enum column
* Assumes that the table name is valid and will not allow SQL injection
*******************************************************************************/
function getEnumValues($table, $column)
{
  // Check that the column name is valid
  $validColumns = getColumns($table);
  if (!in_array($column, $validColumns))
    return false;

  $query = "SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME = '{$table}' AND TABLE_SCHEMA = '" . MYSQL_DB . "' AND COLUMN_NAME = '{$column}'";
  $result = mysql_query($query);

  if (!$result)
    return false;

  $enum = mysql_result($result, 0);
  $enum = str_replace("enum('", "", $enum);
  $enum = str_replace("')", "", $enum);
  $values = explode("','", $enum);

  return $values;
}


/*******************************************************************************
* Deletes the given courseId(s) and all related data.  Accepts variable number
* of parameters or an array.
* Returns true or false to indicate if the course was successfully deleted
*******************************************************************************/
function deleteCourse($courseId)
{
  $rtn = true;

  /* Get courseIds */
  if (is_array($courseId))
    $courseIds = $courseId;
  else
    $courseIds = func_get_args();


  // Create WHERE string for these course ids
  $where = "";
  foreach ($courseIds as $courseId)
  {
    // Check that courseId is numeric before query
    if (!is_numeric($courseId))
      $rtn = false;
    else
      $where .= " OR courseId=" . $courseId;
  }


  if ($rtn && $where != "")
  {
    // Remove initial OR
    $where = substr($where, 3);

    $query = "DELETE FROM courses WHERE{$where}";
    $rtn = (mysql_query($query) && $rtn);

    // Delete related data
    if ($rtn)
    {
      $query = "DELETE FROM enrollments WHERE{$where}";
      $rtn = (mysql_query($query) && $rtn);

      // Get assignment id's for this course
      $query = "SELECT assignmentId FROM assignments WHERE{$where}";
      $result = mysql_query($query);
      $assignmentWhere = "";
      if ($result && mysql_num_rows($result) > 0)
      {
        // Create WHERE string for these assignment ids
        $assignmentWhere = "";
        while ($assignment = mysql_fetch_assoc($result))
          $assignmentWhere .= " OR assignmentId=" . $assignment['assignmentId'];
      }

      if ($assignmentWhere != "")
      {
        $assignmentWhere = substr($assignmentWhere, 3);
        $query = "DELETE FROM assignments WHERE{$where}";
        $rtn = (mysql_query($query) && $rtn);
      }

      // Get userEvents submitted for this course or its assignments
      $query = "";
      if ($assignmentWhere != "")
        $query = "SELECT userEventId FROM assignmentEvents WHERE{$assignmentWhere} UNION ";
      $query .= "SELECT userEventId FROM courseEvents WHERE{$where}";
      $result = mysql_query($query);
      if ($result && mysql_num_rows($result) > 0)
      {
        // Create array of userEvents to be deleted
        $arr = array();
        while ($userEvent = mysql_fetch_assoc($result))
          array_push($arr, $userEvent['userEventId']);
        // Remove these userEvents
        deleteUserEvent($arr);
      }

      // Delete from assignmentEvents
      if ($assignmentWhere != "")
      {
        $query = "DELETE FROM assignmentEvents WHERE{$assignmentWhere}";
        $rtn = (mysql_query($query) && $rtn);
      }

      // Delete from courseEvents
      $query = "DELETE FROM courseEvents WHERE{$where}";
      $rtn = (mysql_query($query) && $rtn);
    }
  }

  return $rtn;
}

/*******************************************************************************
* Updates data for the given courseId
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the user was successfully inserted
*******************************************************************************/
function updateCourseInfo($courseId, $profId, $name, $section, $semester, $comment, $schoolId)
{
  // Check that courseId and profId are numeric before query
  if (!is_numeric($courseId) || !is_numeric($profId) || !is_numeric($schoolId))
    return false;

  $query = "UPDATE courses SET profId = $profId, name = '{$name}', section = '{$section}', semester = '{$semester}', comment = '{$comment}', schoolId={$schoolId} WHERE courseId = '{$courseId}'";
  return mysql_query($query);
}

/*******************************************************************************
* Creates new course with the given parameters
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the user was successfully inserted
*******************************************************************************/
function addNewCourse($profId, $name, $section, $semester, $comment, $schoolId)
{
  // Check that profId is numeric before query
  if (!is_numeric($profId) || !is_numeric($schoolId))
    return false;

  $query = "INSERT INTO courses (profId, name, section, semester, comment, schoolId) VALUES ({$profId}, '{$name}', '{$section}', '{$semester}', '{$comment}', {$schoolId})";
  return mysql_query($query);
}

/*******************************************************************************
* Enrolls the given userId in the given course
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the user was successfully enrolled
*******************************************************************************/
function courseEnroll($userId, $courseId)
{
  // Check that userId and courseId are numeric before query
  if (!is_numeric($userId) || !is_numeric($courseId))
    return false;

  $query = "INSERT INTO enrollments (courseId, studentId) VALUES ({$courseId}, {$userId})";
  $rtn = mysql_query($query);

  // Add notification
  if ($rtn)
  {
    // Get info needed for email
    $result = mysql_query('SELECT userName, courseName, courseSection, courseProfId FROM enrollmentsView WHERE studentId='.$userId.' AND courseId='.$courseId);
    if ($result && $info = mysql_fetch_assoc($result))
    {
      $content = $info['userName'] . ' enrolled in the following course: ' . $info['courseSection'] . '<br/>';
      setupNotify($info['courseProfId'], NOTIFY_ROSTER_CHANGE, $content, $info['courseSection'].' - New Student');
    }
  }

  return $rtn;
}

/*******************************************************************************
* Drops the given userId from the given course.
* Assumes that the caller has already checked that the parameters are valid.
* This removes the database entries that record a userEvent for a course or
* assignment but does not delete the userEvent.
* Returns true or false to indicate if the course was successfully dropped.
*******************************************************************************/
function courseDrop($userId, $courseId)
{
  // Check that userId and courseId are numeric before query
  if (!is_numeric($userId) || !is_numeric($courseId))
    return false;

  $rtn = true;

  // Get info needed for email
  $result = mysql_query('SELECT userName, courseName, courseSection, courseProfId FROM enrollmentsView WHERE studentId='.$userId.' AND courseId='.$courseId);
  if ($result)
    $info = mysql_fetch_assoc($result);

  $query = "DELETE FROM enrollments WHERE courseId={$courseId} AND studentId={$userId} LIMIT 1";
  $rtn = (mysql_query($query) && $rtn);

  // Add notification
  if ($rtn && $info !== false)
  {
    $content = $info['userName'] . ' dropped the following course: ' . $info['courseSection'] . '<br/>';
    setupNotify($info['courseProfId'], NOTIFY_ROSTER_CHANGE, $content, $info['courseSection'].' - Student Dropped');
  }

  // Delete related data
  if ($rtn)
  {
    // Get all userEvents for this course
    $where = "";
    if (($userEvents = getUserReports($userId)) !== false)
      foreach ($userEvents as $userEvent)
        $where .= " OR userEventId=" . $userEvent['userEventId'];

    /* Delete all related data */
    if ($where != "")
    {
       // Remove initial OR
      $where = substr($where, 3);

      // Get assignment id's for this course
      $query = "SELECT assignmentId FROM assignments WHERE courseId={$courseId}";
      $result = mysql_query($query);
      $assignmentWhere = "";
      if ($result && mysql_num_rows($result) > 0)
      {
        // Create WHERE string for these assignment ids
        $assignmentWhere = "";
        while ($assignment = mysql_fetch_assoc($result))
          $assignmentWhere .= " OR assignmentId=" . $assignment['assignmentId'];
      }

      if ($assignmentWhere != "")
      {
        // Remove initial OR
        $assignmentWhere = substr($assignmentWhere, 3);

        // Delete from assignmentEvents
        $query = "DELETE FROM assignments WHERE{$where} AND ($assignmentWhere)";
        $rtn = (mysql_query($query) && $rtn);
      }

      // Delete from courseEvents
      $query = "DELETE FROM courseEvents WHERE courseId={$courseId} AND ({$where})";
      $rtn = (mysql_query($query) && $rtn);
    }
  }

  return $rtn;
}

/*******************************************************************************
* Gets the courses that the given userId is enrolled in if userId is set.
* Otherwise, it gets all courses.  lower and total are the limits to be put on
* the query.  The limit is only applied if userId == false
* Returns an array of arrays with the arrays having indices 'courseId', 'profId',
* 'section', 'name', 'semester', and 'comment' for each course or false if
* userId is invalid or no courses are found
*******************************************************************************/
function getCourses($userId = false, $lower = false, $total = false, $status)
{
  $rtn = array();

  // Create limit string
  if (is_numeric($lower) && is_numeric($total) && $lower >= 0 && $total > 0)
    $limit = " LIMIT {$lower}, {$total}";
  else
    $limit = "";

  // Find courses for the user
  if ($userId !== false)
  {
    // Check that userId is numeric before query
    if (!is_numeric($userId))
      return false;

    $where = '';
    if ($status != null)
      $where = ' AND courseId IN (SELECT courseId FROM courses WHERE status=\'' . $status . '\')';

    $query = "SELECT courseId FROM enrollments WHERE studentId = {$userId} {$where}";

    if (($result = mysql_query($query)) === false)
      return false;

    // Go through each courseId and get information about the course
    while ($course = mysql_fetch_assoc($result))
    {
      $query = "SELECT courseId, profId, section, name, semester, comment FROM courses WHERE courseId = {$course['courseId']} LIMIT 1";
      if ($courseResult = mysql_query($query))
        if ($row = mysql_fetch_assoc($courseResult))
        {
          // Make fields HTML safe
          foreach ($row as &$col)
            $col = htmlspecialchars($col);
          array_push($rtn, $row);
        }
    }
  }
  // Get all courses
  else
  {
    $where = array();

    // Set school id
    global $_SESSION;
    if (isset($_SESSION['schoolId']))
      $where[] = "schoolId={$_SESSION['schoolId']}";

    if ($status != null)
      $where[] = 'status=\'' . $status . '\'';

    if (empty($where))
      $where = "";
    else
      $where = "WHERE " . implode(" AND ", $where);

    $query = "SELECT * FROM courses {$where} ORDER BY section{$limit}";
    if ($courseResult = mysql_query($query))
      while ($row = mysql_fetch_assoc($courseResult))
      {
        // Make fields HTML safe
        foreach ($row as &$col)
          $col = htmlspecialchars($col);
        array_push($rtn, $row);
      }
  }

  if (count($rtn) == 0)
    return false;

  // Sort the courses by semester and name
  uasort($rtn, "sortCourses");

  return $rtn;
}


/*******************************************************************************
* Gets all students in the course.
* Returns an array of arrays with the arrays having indices 'userId', 'name',
* and 'email' for each student
*******************************************************************************/
function getCourseStudents($courseId)
{
  $rtn = array();

  // Find course ids for the user
  $query = "SELECT studentId FROM enrollments WHERE courseId = {$courseId}";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  // Go through each id and get information about the student
  while ($id = mysql_fetch_assoc($result))
  {
    $query = "SELECT userId, name, email FROM users WHERE userId = {$id['studentId']} LIMIT 1";
    if ($userResult = mysql_query($query))
      if ($row = mysql_fetch_assoc($userResult))
        array_push($rtn, $row);
  }

  return $rtn;
}

/*******************************************************************************
* Gets the courses that the given profId is teaching.  $connected is used to
* indicate whether there is already a MySQL connection to use.
* Returns an array of arrays with the arrays having indices 'courseId', 'section',
* 'name', 'semester' and 'comment' for each course
*******************************************************************************/
function getProfCourses($profId, $status = null)
{
  $rtn = array();

  $where = '';
  if ($status != null)
    $where = ' AND status=\'' . $status . '\'';

  // Find courses for the professor
  $query = "SELECT * FROM courses WHERE profId = {$profId} {$where} ORDER BY section ASC";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  while ($row = mysql_fetch_assoc($result))
  {
    foreach ($row as &$col)
      $col = htmlspecialchars($col);
    array_push($rtn, $row);
  }

  return $rtn;
}


/*******************************************************************************
* Callback function for sorting course array
*******************************************************************************/
function sortCourses($a, $b)
{
  $rtn = 0;

  $aSem = splitSemester($a['semester']);
  $bSem = splitSemester($b['semester']);

  // Compare year
  if ($aSem['year'] < $bSem['year'])
    $rtn = -1;
  else if ($aSem['year'] > $bSem['year'])
    $rtn = 1;

  // Compare semester if needed
  else if ($aSem['season'] < $bSem['season'])
    $rtn = -1;
  else if ($aSem['season'] > $bSem['season'])
    $rtn = 1;

  // Compare course section if needed
  else
    $rtn = strcmp($a['section'], $b['section']);

  return $rtn;
}

/*******************************************************************************
* Splits the semester into the season as an int (0 = Spring, 1 = Summer, ...)
* and the year
*******************************************************************************/
function splitSemester($semester)
{
  $season = -1;
  $idx = strpos($semester, " ");
  if ($idx >= 0)
  {
    $season = substr($semester, 0, $idx);

    if ($season == "Spring")
      $season = 0;
    else if ($season == "Summer")
      $season = 1;
    else if ($season == "Fall")
      $season = 2;
    else if ($season == "Winter")
      $season = 3;

    $year = (int) substr($semester, $idx+1);
  }
  return array("year"=>$year, "season"=>$season);
}

/*******************************************************************************
* Adds database entry to request an account type change.
* @param int $userId User id requesting the change
* @param string $acctType The account type to change the user to.
*               Valid values are:
*                 student
*                 professor
*                 admin
* @param string $comment The comment to add to the request
* @return mixed Returns req_id of data in database on successful insertion and
*               false otherwise
*******************************************************************************/
function requestAcctChange($userId, $acctType, $comment = null)
{
  // Check that userId is numeric and exists in database before query
  if (!is_numeric($userId) || !userIdExists($userId))
    return false;

  // Check that account type is valid
  if (
    $acctType != 'student' &&
    $acctType != 'professor' &&
    $acctType != 'admin'
  )
    return false;

  // Setup columns and data
  $cols = array(userId, acct_type);
  $data = array($userId, '\''.$acctType.'\'');
  if ($comment != null)
  {
    $cols[] = 'comment';
    $data[] = '\'' . mysql_real_escape_string($comment) . '\'';
  }

  // Insert record
  $query = 'INSERT INTO request_acct_change (' . implode(',', $cols) . ') VALUES (' . implode(',', $data) . ')';
  $rtn = mysql_query($query);

  // Check for error and get auto_increment value if successful
  if ($rtn !== false)
  {
    $tmp = mysql_insert_id();
    if ($tmp !== false)
      $rtn = $tmp;

    /* Add notification */
    $content = "A user requested an account type change to {$acctType}.<br/>"; // Default message
    
    // Get user information
    $result = mysql_query('SELECT name, acctType FROM users WHERE userId='.$userId);
    if ($result && $info = mysql_fetch_assoc($result))
      $content = $info['name'] . " requested an account type change from {$info['acctType']} to {$acctType}.<br/>";
    
    // Get admins and set up notification
    if (($result = mysql_query('SELECT userId FROM users WHERE acctType="admin"')) !== false)
      while ($info = mysql_fetch_assoc($result))
        setupNotify($info['userId'], NOTIFY_REQUEST, $content, 'New Request');
  }

  return $rtn;
}

/*******************************************************************************
* Deletes an account change request.  If $userId is set, then it makes sure the
* request is for the given user;
*
* @param int $req_id Id of request to delete
* @param int $userId Id of user for whom the request must belong
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - Request id invalid (Ie. Does not exist)
*               2 - User id does not match
*               3 - MySQL error
*******************************************************************************/
function deleteAcctChangeRequest($req_id, $userId = null)
{
  // Check that req_id is numeric
  if (!is_numeric($req_id))
    return 1;

  // Get request user
  $result = mysql_query("SELECT userId FROM request_acct_change WHERE req_id={$req_id}");
  if (!$result || mysql_num_rows($result) == 0)
    return 1;

  if ($userId !== null && $userId != mysql_result($result, 0))
    return 2;

  // Delete rows
  $sql = 'DELETE FROM request_acct_change WHERE req_id='.$req_id;
  $result = mysql_query($sql);
  if ($result === false)
    return 3;

  return 0;
}

/*******************************************************************************
* Accepts an account change request.
*
* @param int $req_id Id of request to accept
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - Request id invalid (Ie. Does not exist)
*               2 - MySQL error
*               3 - MySQL error when changing status
*******************************************************************************/
function acceptAcctChangeRequest($req_id)
{
  // Check that req_id is numeric
  if (!is_numeric($req_id))
    return 1;

  // Get request user
  $result = mysql_query("SELECT userId, acct_type FROM request_acct_change WHERE req_id={$req_id}");
  if (!$result || mysql_num_rows($result) == 0)
    return 1;

  // Update account type
  if (!setUserAcctType(mysql_result($result, 0, 0), mysql_result($result, 0, 1)))
    return 2;

  // Update request status
  $sql = 'UPDATE request_acct_change SET status="accepted" WHERE req_id='.$req_id;
  $result = mysql_query($sql);
  if ($result === false)
    return 3;

  return 0;
}

/*******************************************************************************
* Rejects an account change request.
*
* @param int $req_id Id of request to reject
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - Request id invalid (Ie. Does not exist)
*               2 - MySQL error changing status
*******************************************************************************/
function rejectAcctChangeRequest($req_id)
{
  // Check that req_id is numeric
  if (!is_numeric($req_id))
    return 1;

  // Get request user
  $result = mysql_query("SELECT userId, acct_type FROM request_acct_change WHERE req_id={$req_id}");
  if (!$result || mysql_num_rows($result) == 0)
    return 1;

  // Update request status
  $sql = 'UPDATE request_acct_change SET status="rejected" WHERE req_id='.$req_id;
  $result = mysql_query($sql);
  if ($result === false)
    return 2;

  return 0;
}

/*******************************************************************************
* Gets number of pending requests
*
* @return mixed Returns array on success, otherwise false.  The array elements 
*               are associative arrays with the "name" of the request and the 
*               number of pending requests.
*******************************************************************************/
function getNumPendingRequests()
{
  // Return array
  $rtn = array();

  // Make query and process results
  $query = 'SELECT COUNT(*) FROM request_acct_change WHERE status=\'pending\'';
  if (($result = mysql_query($query)) === false)
    return false;
  $rtn[] = array("name"=>"Account type change", "num"=>mysql_result($result, 0, 0));
  
  // Make query and process results
  $query = 'SELECT COUNT(*) FROM request_acct_deletions WHERE status=\'pending\'';
  if (($result = mysql_query($query)) === false)
    return false;
  $rtn[] = array("name"=>"Account deletion", "num"=>mysql_result($result, 0, 0));

  return $rtn;
}

/*******************************************************************************
* Gets requests for account type change.
*
* @param string $status Finds only requests with this status
* @param int $userId User id to find requests for
*
* @return mixed Returns array of data for requests on success, otherwise false
*******************************************************************************/
function getAcctTypeRequests($status, $userId = null)
{
  $query = 'SELECT * FROM request_acct_change_view';

  $where = array();

  // Check if status is set
  if ($status != null)
    $where[] = 'status="'.$status.'"';

  // Check if user id is set and valid
  if ($userId != null)
    if (!is_numeric($userId))
      return false;
    else
      $where[] = 'userId='.$userId;

  // Add WHERE clause
  if (count($where) > 0)
    $query .= ' WHERE ' . implode(' AND ', $where);

  // Add order clause
  $query .= ' ORDER BY req_date DESC';

  // Make query and process results
  $rtn = mysql_query($query);
  if ($rtn !== false)
  {
    $tmp = array();
    while ($row = mysql_fetch_array($rtn, MYSQL_ASSOC))
      $tmp[] = $row;
    $rtn = $tmp;
  }

  return $rtn;
}

/*******************************************************************************
* Adds database entry to request account removal.
* @param int $userId User id requesting the removal
*
* @return bool Returns true on successful insertion and false otherwise
*******************************************************************************/
function requestAcctRemoval($userId)
{
  // Check that userId is numeric and exists in database before query
  if (!is_numeric($userId) || !userIdExists($userId))
    return false;

  // Insert record
  $query = 'INSERT INTO request_acct_deletions (userId) VALUES (' . $userId . ') ON DUPLICATE KEY UPDATE status="pending", req_date=NOW()';
  $rtn = mysql_query($query);
  
  if ($rtn)
  {
    /* Add notification */
    $content = "A user requested to have their account removed.<br/>"; // Default message
    
    // Get user information
    $result = mysql_query('SELECT name, acctType FROM users WHERE userId='.$userId);
    if ($result && $info = mysql_fetch_assoc($result))
      $content = $info['name'] . " requested to have their account removed.<br/>";
    
    // Get admins and set up notification
    if (($result = mysql_query('SELECT userId FROM users WHERE acctType="admin"')) !== false)
      while ($info = mysql_fetch_assoc($result))
        setupNotify($info['userId'], NOTIFY_REQUEST, $content, 'New Request');
  }

  return $rtn;
}

/*******************************************************************************
* Deletes an account removal request.
*
* @param int $userId Id of user that the request was for
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - User id invalid (Ie. Not a number)
*               2 - MySQL error
*******************************************************************************/
function deleteAcctRemovalRequest($userId)
{
  // Check that user_id is numeric
  if (!is_numeric($userId))
    return 1;

  // Delete rows
  $sql = 'DELETE FROM request_acct_deletions WHERE userId='.$userId;
  $result = mysql_query($sql);
  if ($result === false)
    return 2;

  return 0;
}

/*******************************************************************************
* Accepts an account removal request.
*
* @param int $userId Id of user to delete
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - Request does not exist
*               2 - MySQL error
*******************************************************************************/
function acceptAcctRemovalRequest($userId)
{
  // Check that userId is numeric
  if (!is_numeric($userId))
    return 1;

  // Get request user
  $result = mysql_query("SELECT userId FROM request_acct_deletions WHERE userId={$userId}");
  if (!$result || mysql_num_rows($result) == 0)
    return 1;

  // Delete user
  if (!deleteUser($userId))
    return 2;

  return 0;
}

/*******************************************************************************
* Rejects an account removal request.
*
* @param int $userId Id of user to reject request for
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - Request does not exist
*               2 - MySQL error
*******************************************************************************/
function rejectAcctRemovalRequest($userId)
{
  // Check that userId is numeric
  if (!is_numeric($userId))
    return 1;

  // Get request user
  $result = mysql_query("SELECT userId FROM request_acct_deletions WHERE userId={$userId}");
  if (!$result || mysql_num_rows($result) == 0)
    return 1;

  // Delete request because the user will not be able to see that the request
  $sql = 'UPDATE request_acct_deletions SET status="rejected" WHERE userId='.$userId;
  $result = mysql_query($sql);
  if ($result === false)
    return 2;

  return 0;
}

/*******************************************************************************
* Gets requests for account removal.
*
* @param string $status Finds only requests with this status
* @param int $userId User id to find requests for
*
* @return mixed Returns array of data for requests on success, otherwise false
*******************************************************************************/
function getAcctRemovalRequests($status, $userId = null)
{
  $query = 'SELECT * FROM request_acct_deletions_view';

  $where = array();

  // Check if status is set
  if ($status != null)
    $where[] = 'status="'.$status.'"';

  // Check if user id is set and valid
  if ($userId != null)
    if (!is_numeric($userId))
      return false;
    else
      $where[] = 'userId='.$userId;

  // Add WHERE clause
  if (count($where) > 0)
    $query .= ' WHERE ' . implode(' AND ', $where);

  // Add order clause
  $query .= ' ORDER BY req_date DESC';

  // Make query and process results
  $rtn = mysql_query($query);
  if ($rtn !== false)
  {
    $tmp = array();
    while ($row = mysql_fetch_array($rtn, MYSQL_ASSOC))
      $tmp[] = $row;
    $rtn = $tmp;
  }

  return $rtn;
}

/*******************************************************************************
* Adds an assignments for a particular course
*
* @param int $courseId Course id to get assignments for.
* @param int $name Name of assignment
* @param int $openTime Timestamp for when the assignment opens
* @param int $closeTime Timestamp for when the assignment closes
*
* @return mixed Returns assignmentId for assignment on success and false
*               otherwise.
*******************************************************************************/
function addAssignment($courseId, $name, $openTime, $closeTime)
{
  // Check that courseId is numeric and exists in database before query
  if (!is_numeric($courseId) || !courseExists($courseId))
    return false;

  // Check that open and close times are timestamps
  if (!is_int($openTime) || !is_int($closeTime))
    return false;

  $values = array(
    $courseId,
    '\'' . mysql_real_escape_string($name) . '\'',
    date("'Y-m-d H:i:s'", $openTime),
    date("'Y-m-d H:i:s'", $closeTime),
  );

  $query = 'INSERT INTO assignments (courseId, name, openTime, closeTime) VALUES (' . implode(',', $values) . ')';
  $rtn = mysql_query($query);

  // Get assignment id
  if ($rtn !== false)
  {
    $tmp = mysql_insert_id();
    if ($tmp !== false)
    {
      $rtn = $tmp;

      // Add notification
      $course = getCourseInfo($courseId);
      $content = 'The following assignment has been added: ' . $name . '<br/>';
      if ($students = getCourseStudents($courseId))
        foreach ($students as $user)
          setupNotify($user['userId'], NOTIFY_ASSIGNMENT_NEW, $content, $course['section'].' - New Assignment Posted');
    }
  }

  return $rtn;
}

/*******************************************************************************
* Updates information for an assignments
*
* @param int $assignmentId Assignment id to edit
* @param int $courseId Course id to get assignments for.
* @param int $name Name of assignment
* @param int $openTime Timestamp for when the assignment opens
* @param int $closeTime Timestamp for when the assignment closes
*
* @return mixed Returns number of affected rows (Should be 1)
*******************************************************************************/
function updateAssignment($assignmentId, $courseId, $name, $openTime, $closeTime)
{
  // Check that assignmentId is numeric
  if (!is_numeric($assignmentId))
    return false;

  // Check that courseId is numeric and exists in database before query
  if (!is_numeric($courseId) || !courseExists($courseId))
    return false;

  // Check that open and close times are timestamps
  if (!is_int($openTime) || !is_int($closeTime))
    return false;

  $values = array(
    'courseId' => $courseId,
    'name' => '\'' . mysql_real_escape_string($name) . '\'',
    'openTime' => date("'Y-m-d H:i:s'", $openTime),
    'closeTime' => date("'Y-m-d H:i:s'", $closeTime),
  );

  $set = array();
  foreach ($values as $key=>$value)
    $set[] = $key . '=' . $value;
  $set = implode(',', $set);

  $query = 'UPDATE assignments SET '. $set . ' WHERE assignmentId=' . $assignmentId;
  mysql_query($query);

  return mysql_affected_rows();
}

/*******************************************************************************
* Sets mandatory doclet options for an assignment
*
* @param int $assignmentId Assignment id to set options for
* @param array $doclets Array of doclet ids that are to be run for assignment
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - Assignment id invalid (Ie. Does not exist)
*               2 - At least one doclet id is invalid
*               3 - MySQL error
*******************************************************************************/
function setAssignmentOptions($assignmentId, $doclets)
{
  // Check that assignmentId is numeric and exists
  if (!is_numeric($assignmentId) || getAssignment($assignmentId) === false)
    return 1;

  // Check that doclet ids exist
  if (count($doclets) > 0)
  {
    $where = array();
    foreach ($doclets as $doc)
    {
      if (is_numeric($doc))
        $where[] = 'docletId='.$doc;
      else
        return 2;
    }
    $query = 'SELECT COUNT(*) FROM doclets WHERE ' . implode(' OR ', $where);
    $result = mysql_query($query);
    if (mysql_result($result, 0) != count($doclets))
      return 2;
  }

  // Delete existing options
  $sql = 'DELETE FROM userEventsAssignmentOptions WHERE assignmentId='.$assignmentId;
  $result = mysql_query($sql);
  if ($result === false)
    return 3;

  // Add each option
  if (count($doclets) > 0)
    foreach ($doclets as $doc)
    {
      $query = 'INSERT INTO userEventsAssignmentOptions (assignmentId, docletId) VALUES (' . $assignmentId . ', ' . $doc . ')';
      if (mysql_query($query) === false)
        return 3;
    }

  return 0;
}

/*******************************************************************************
* Gets mandatory doclet options for an assignment
*
* @param int $assignmentId Assignment id to set options for
*
* @return mixed Returns array of doclet ids if any are found.  Otherwise, false
*******************************************************************************/
function getAssignmentOptions($assignmentId)
{
  // Check that assignmentId is numeric and exists
  if (!is_numeric($assignmentId) || getAssignment($assignmentId) === false)
    return false;

  // Delete existing options
  $sql = 'SELECT docletId FROM userEventsAssignmentOptions WHERE assignmentId='.$assignmentId;
  $result = mysql_query($sql);
  if ($result === false)
    return false;

  // Construct array
  $rtn = array();
  $rows = mysql_num_rows($result);
  for ($i = 0; $i < $rows; $i++)
    $rtn[] = mysql_result($result, $i);

  return $rtn;
}

/*******************************************************************************
* Gets assignments for a particular course and postings if a user id is sent as
* a parameter.
*
* @param int $courseId Course id to get assignments for.
* @param array params An associative array of parameters with can include any
*                     combination of the following:
*                       'cols' - A list of columns to fetch
*                       'userId' - If set, gets the assignments for the user
*                       'where' - Additional WHERE clause to add to query
*                       'order' - ORDER clause to add to query
*                       'time' - Timestamp - If set, only assignments that can
*                         be submitted at this time are shown
* @return mixed Returns array of data for assignments on success and false
*               otherwise.
*******************************************************************************/
function getAssignments($courseId, $params = array())
{
  // Check that courseId is numeric and exists in database before query
  if (!is_numeric($courseId) || !courseExists($courseId))
    return false;

  // Default parameters
  $userId = null;
  $cols = '*';
  $where = array('courseId='.$courseId);
  $order = ' ORDER BY openTime ASC, closeTime ASC';

  // Check user supplied parameters.
  foreach ($params as $key=>$value)
  {
    switch ($key)
    {
      case 'userId':
        if (!is_numeric($value) || !userIdExists($value))
          return false;
        else
          $userId = $value;
        break;
      case 'cols':
        if (is_array($value))
          $cols = implode(',', $value);
        else if (is_string($value))
          $cols = $value;
        break;
      case 'where':
        if (is_array($value))
          $where[] = '('. implode(' AND ', $value).')';
        else if (is_string($value))
          $where[] = '('.$value.')';
        break;
      case 'where':
        $order = ' ORDER BY ' . $value;
        break;
      case 'time':
        if (is_int($value))
        {
          $time = date("'Y-m-d H:i:s'", $value);
          $where[] = 'openTime<=' . $time . ' AND closeTime>=' . $time;
        }
        else
          return false;
        break;
    }
  }

  // Construct where clause
  $where = implode(' AND ', $where);
  if ($where != '')
    $where = ' WHERE ' . $where;

  $query = 'SELECT ' . $cols . ' FROM assignments' . $where . $order;
  $rtn = mysql_query($query);

  if ($rtn !== false)
  {
    $tmp = array();
    while ($row = mysql_fetch_array($rtn, MYSQL_ASSOC))
    {
      // Get submissions if userId is set
      if ($userId !== null)
      {
        $row['submissions'] = array();
        $query = 'SELECT userEventId, dateTime as submission_date, compilationError FROM userEvents WHERE userEventId IN (SELECT userEventId FROM assignmentEvents WHERE assignmentId=' . $row['assignmentId'] . ' AND userId=' . $userId . ') ORDER BY dateTime ASC';
        $result = mysql_query($query);
        $version = 1;
        while ($submission = mysql_fetch_array($result, MYSQL_ASSOC))
        {
          $submission['name'] = $row['name'] . ' (Version ' . $version++ . ')';
          $submission['comment'] = getUserEventComment($submission['userEventId'], true);
          $row['submissions'][] = $submission;
        }
      }

      $tmp[] = $row;
    }
    $rtn = $tmp;
  }

  return $rtn;
}

/*******************************************************************************
* Gets a specific assignment
*
* @param int $assignmentId Assignment id to fetch
* @param array params An associative array of parameters with can include any
*                     combination of the following:
*                       'cols' - A list of columns to fetch
*
* @return mixed Returns associative array of data for assignments on success
*               and false otherwise.
*******************************************************************************/
function getAssignment($assignmentId, $params = array())
{
  // Check that assignmentId is numeric
  if (!is_numeric($assignmentId))
    return false;

  // Construct WHERE clause
  $where = ' WHERE assignmentId='.$assignmentId;

  // Default parameters
  $cols = '*';

  // Check user supplied parameters.
  foreach ($params as $key=>$value)
  {
    switch ($key)
    {
      case 'cols':
        if (is_array($value))
          $cols = implode(',', $value);
        else if (is_string($value))
          $cols = $value;
        break;
    }
  }

  $query = 'SELECT ' . $cols . ' FROM assignments' . $where;
  $rtn = mysql_query($query);

  if ($rtn !== false)
    $rtn = mysql_fetch_array($rtn, MYSQL_ASSOC);

  return $rtn;
}

/*******************************************************************************
* Deletes a specific assignment
*
* @param int $assignmentId Assignment id to fetch
* @return bool Returns true or false to indicate if the assignment was deleted.
*******************************************************************************/
function deleteAssignment($assignmentId)
{
  // Check that courseId is numeric and exists in database before query
  if (!is_numeric($assignmentId))
    return false;

  // Construct WHERE clause
  $where = ' WHERE assignmentId='.$assignmentId;

  $query = 'DELETE FROM assignments' . $where;
  $rtn = mysql_query($query);

  return $rtn;
}

/*******************************************************************************
* Inserts row into database indicating that the given userEventId was submitted
* for the given assignmentId.
*
* @param int $userId Id of user.  This is used to check that the user can submit
*                   for the assignment.
* @param int $userEventId Id of user event.
* @param int $assignmentId Id of assignment.
*
* @return int Returns one of the following status codes:
*               0 - Successful
*               1 - Variable types invalid
*               2 - Assignment does not exist
*               3 - User not in course
*               4 - Database error
*******************************************************************************/
function recordReportForAssignment($userId, $userEventId, $assignmentId)
{
  // Check that ids are numeric before query
  if (!is_numeric($userId) || !is_numeric($userEventId) || !is_numeric($assignmentId))
    return 1;

  // Check that the given assignmentId exists
  if (($assignment = getAssignment($assignmentId)) === false)
    return 2;

  // Check that the user is in the course for the assignment
  if (!isUserInCourse($userId, $assignment['courseId']))
    return 3;

  $query = 'INSERT INTO assignmentEvents (userEventId, assignmentId) VALUES (' . $userEventId . ', ' . $assignmentId . ')';
  if (!mysql_query($query))
    return 4;

  // Add notification
  $user = getUserInfoById($userId);
  $course = getCourseInfo($assignment['courseId']);
  $content = 'A new submission was made by ' . $user['name'] . ' for the following assignment: ' . $assignment['name'] . '<br/>';
  setupNotify($course['profId'], NOTIFY_STUDENT_SUBMISSION, $content, $course['section'].' - New Student Submission');

  return 0;
}

/*******************************************************************************
* Returns the number of reports submitted for the given assignmentId.
*
* @param int $assignmentId Id of assignment.
*
* @return int Returns the number of reports submitted or -1 on error
*******************************************************************************/
function numAssignmentSubmissions($assignmentId)
{
  // Check that ids are numeric before query
  if (!is_numeric($assignmentId))
    return -1;

  // Check that the given assignmentId exists
  if (($assignment = getAssignment($assignmentId)) === false)
    return -1;

  $query = 'SELECT COUNT(*) FROM assignmentEvents WHERE assignmentId=' . $assignmentId;
  if (($result = mysql_query($query)) === false)
    return -1;

  $num = mysql_result($result, 0);
  if ($num === false)
    return -1;

  return (int)$num;
}

/*******************************************************************************
* Sets a comment into database for the given userEventId.  This comment
* appears on the dropbox page.
*
* @param int $userEventId Id of user event.
* @param string $comment Comment
*
* @return int Returns one of the following status codes:
*               0 - Successful
*               1 - Variable types invalid
*               2 - Assignment does not exist
*               3 - Database error
*******************************************************************************/
function setUserEventComment($userEventId, $comment)
{
  // Check that id is numeric before query
  if (!is_numeric($userEventId))
    return 1;

  // Check that the given userEventId exists
  if (!userEventExists($userEventId))
    return 2;

  // Escape comment
  $comment = mysql_real_escape_string($comment);

  // Insert
  if (!empty($comment))
  {
    $query = 'INSERT INTO userEventComments (userEventId, comment) VALUES (' . $userEventId . ', "' . $comment . '")';
    $query .= ' ON DUPLICATE KEY UPDATE comment="' . $comment . '"';
    if (!mysql_query($query))
      return 3;
  }

  // Add notification
  // Get info needed for email
  $result = mysql_query('SELECT userId, userName, courseName, courseSection, assignmentName FROM userEventsView WHERE userEventId='.$userEventId);
  if ($result && $info = mysql_fetch_assoc($result))
  {
    $content =  'A new comment was made for the following assignment: ' . $info['assignmentName'] . '<br/>';
    setupNotify($info['userId'], NOTIFY_ASSIGNMENT_COMMENT, $content, $info['courseSection'].' - New Submission Comment');
  }

  return 0;
}

/*******************************************************************************
* Gets comment for the given userEventId.
*
* @param int $userEventId Id of user event.
* @param bool $htmlsafe Should the comment be escaped for HTML and have newlines
*                       replaced with <br/>.
*
* @return mixed Returns comment if found and false otherwise.
*******************************************************************************/
function getUserEventComment($userEventId, $htmlsafe = false)
{
  // Check that id is numeric before query
  if (!is_numeric($userEventId))
    return false;

  // Insert
  $query = 'SELECT comment FROM userEventComments WHERE userEventId=' . $userEventId;
  if (($result = mysql_query($query)) === false)
    return false;

  // Get comment
  if (($comment = @mysql_result($result, 0)) === false)
    return false;

  // Escape comment
  if ($htmlsafe)
    $comment = nl2br(htmlentities($comment));

  return $comment;
}

/*******************************************************************************
* Checks if a userEventId exists
*
* @param int $userEventId Id of user event.
*
* @return bool Returns true if the course exists.
*******************************************************************************/
function userEventExists($userEventId)
{
  // Check that id is numeric before query
  if (!is_numeric($userEventId))
    return false;

  $result = mysql_query("SELECT userEventId FROM userEvents WHERE userEventId={$userEventId} LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/* E-mail Notification Options */
define("NOTIFY_ASSIGNMENT_NEW",           0x00000001);
define("NOTIFY_ASSIGNMENT_DEADLINE_NEAR", 0x00000010);
define("NOTIFY_ASSIGNMENT_OPEN",          0x00000100);
define("NOTIFY_ASSIGNMENT_CLOSE",         0x00001000);
define("NOTIFY_ASSIGNMENT_COMMENT",       0x00010000);
define("NOTIFY_STUDENT_SUBMISSION",       0x00100000);
define("NOTIFY_ROSTER_CHANGE",            0x01000000);
define("NOTIFY_REQUEST",                  0x10000000);
// Use 0x00000002 next

define("NOTIFY_FREQ_ON_ACTION",           0x00000001);
define("NOTIFY_FREQ_ON_HOUR",             0x00000010);
define("NOTIFY_FREQ_ON_HALF_HOUR",        0x00000100);
define("NOTIFY_FREQ_ON_SIX_HOUR",         0x00001000);
define("NOTIFY_FREQ_ON_DAY",              0x00010000);

/*******************************************************************************
* Sets user options for E-mail notification.  Assumes that $notification_types
* and $frequency are valid
*
* @param int $userId Id of user
* @param int $notification_types Bitwise combination of the following:
*                                 NOTIFY_ASSIGNMENT_NEW
*                                 NOTIFY_ASSIGNMENT_DEADLINE_NEAR
*                                 NOTIFY_ASSIGNMENT_OPEN
*                                 NOTIFY_ASSIGNMENT_CLOSE
*                                 NOTIFY_ASSIGNMENT_COMMENT
*                                 NOTIFY_STUDENT_SUBMISSION
*                                 NOTIFY_ROSTER_CHANGE
*                                 NOTIFY_REQUEST
*
* @param int $frequency One of the following:
*                                 NOTIFY_FREQ_ON_ACTION
*                                 NOTIFY_FREQ_ON_HOUR
*                                 NOTIFY_FREQ_ON_HALF_HOUR
*                                 NOTIFY_FREQ_ON_SIX_HOUR
*                                 NOTIFY_FREQ_ON_DAY
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - User id does not exist
*               2 - MySQL error
*******************************************************************************/
function setNotifyOptions($userId, $notification_types, $frequency)
{
  // Check that user id exists
  if (!userIdExists($userId))
    return 1;

  // Check notification options for user
  $result = mysql_query("INSERT INTO userNotificationOptions (userId, notifications, frequency) VALUES ({$userId}, {$notification_types}, {$frequency}) ON DUPLICATE KEY UPDATE notifications={$notification_types}, frequency={$frequency}");

  if (!$result)
    return 2;

  return 0;
}

/*******************************************************************************
* Gets user options for E-mail notification.
*
* @param int $userId Id of user
*
* @return mixed Associative array containing 'notifications' and 'frequency'.
*               False on error.
*******************************************************************************/
function getNotifyOptions($userId)
{
  // Check that user id exists
  if (!userIdExists($userId))
    return false;

  // Check notification options for user
  $result = mysql_query("SELECT notifications, frequency FROM userNotificationOptions WHERE userId={$userId} LIMIT 1");

  if (!$result)
    return false;

  return mysql_fetch_assoc($result);
}

/*******************************************************************************
* Sets up an E-mail notification
*
* @param int $userId Id of user to send to
* @param int $notification_type One of the following:
*                                 NOTIFY_ASSIGNMENT_NEW
*                                 NOTIFY_ASSIGNMENT_DEADLINE_NEAR
*                                 NOTIFY_ASSIGNMENT_OPEN
*                                 NOTIFY_ASSIGNMENT_CLOSE
*                                 NOTIFY_ASSIGNMENT_COMMENT
*                                 NOTIFY_STUDENT_SUBMISSION
*                                 NOTIFY_ROSTER_CHANGE
*                                 NOTIFY_REQUEST
*
* @param string $content Content of E-mail
* @param string $subject Subject of E-mail
*******************************************************************************/
function setupNotify($userId, $notification_type, $content, $subject)
{
  // Check that ids are numeric before query
  if (!is_numeric($userId))
    return false;

  // Check notification options for user
  $result = mysql_query("SELECT frequency FROM userNotificationOptions WHERE userId={$userId} AND {$notification_type} & notifications LIMIT 1");

  // No notification required
  if (!$result || mysql_num_rows($result) == 0)
    return;

  $time = null;

  switch (mysql_result($result, 0))
  {
    case NOTIFY_FREQ_ON_ACTION:
      // Get user e-mail address
      $result = mysql_query("SELECT email FROM users WHERE userId={$userId} LIMIT 1");
      if (!$result || mysql_num_rows($result) == 0)
        return;  // No notification required
      $email = mysql_result($result, 0);

      require_once("generalFunctions.php");
      sendMail($content, $email, null, $subject);
      break;
    case NOTIFY_FREQ_ON_HOUR:
      // Get timestamp
      list($hour, $month, $day, $year) = explode(' ', date("H n j Y"));
      $time = mktime(((int)$hour)+1, 0, 0, $month, $day, $year);
      break;
    case NOTIFY_FREQ_ON_HALF_HOUR:
      // Get timestamp
      list($hour, $min, $month, $day, $year) = explode(' ', date("H i n j Y"));
      if ((int)$min >= 30)
      {
        $hour = ((int)$hour)+1;
        $min = 0;
      }
      else
        $min = 30;
      $time = mktime($hour, $min, 0, $month, $day, $year);
      break;
    case NOTIFY_FREQ_ON_SIX_HOUR:
      // Get timestamp
      list($hour, $month, $day, $year) = explode(' ', date("H n j Y"));
      $hour = (int)$hour;
      if ($hour < 6)
        $hour = 6;
      else if ($hour < 12)
        $hour = 12;
      else if ($hour < 18)
        $hour = 18;
      else
        $hour = 24;
      $time = mktime($hour, 0, 0, $month, $day, $year);
      break;
    case NOTIFY_FREQ_ON_DAY:
      // Get timestamp
      list($month, $day, $year) = explode(' ', date("n j Y"));
      $time = mktime(0, 0, 0, $month, $day+1, $year);
      break;
  }

  // Add notification time to database
  if ($time != null)
  {
    $query = 'INSERT INTO userNotifications (userId, time, content, subject) VALUES ('.
      $userId . ',' .
      $time . ',' .
      '"' . $content . '", ' .
      '"' . $subject . '"' .
      ')';
    mysql_query($query);
  }
}

/*******************************************************************************
* Gets E-mail notifications.
*
* @return mixed Array containing notifications. False on error.
*******************************************************************************/
function getNotifications()
{
  $result = mysql_query("SELECT userNotificationId, userId, content, subject FROM userNotifications WHERE time < ".time());

  if (!$result)
    return false;

  $rtn = array();
  while ($row = mysql_fetch_assoc($result))
    $rtn[] = $row;

  return $rtn;
}

/*******************************************************************************
* Deletes E-mail notifications.
*
* @param array $ids Array of ids to delete
*******************************************************************************/
function deleteNotifications($ids)
{
  $where = array();
  foreach ($ids as $id)
    if (is_numeric($id))
      $where[] = "userNotificationId={$id}";

  if (!empty($where))
  {
    $where = implode(' OR ', $where);
    mysql_query("DELETE FROM userNotifications WHERE {$where}");
  }
}

/*******************************************************************************
* Gets all schools in the database
*
* @return mixed Array of associative arrays containing 'schoolId' and 'school'.
*               False on error.
*******************************************************************************/
function getSchools()
{
  // Check notification options for user
  $result = mysql_query("SELECT schoolId, school FROM schools ORDER BY school ASC");

  if (!$result)
    return false;

  $schools = array();

  while ($row = mysql_fetch_assoc($result))
    $schools[] = $row;

  return $schools;
}

/*******************************************************************************
* Gets a school
*
* @param $schoolId Id of school to search for
*
* @return mixed School name if it exists, otherwise false
*******************************************************************************/
function getSchool($schoolId)
{
  // Check that id is numeric before query
  if (!is_numeric($schoolId))
    return false;

  $result = mysql_query("SELECT school FROM schools WHERE schoolId={$schoolId} LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  $res = mysql_fetch_assoc($result);
  return $res['school'];
}

/*******************************************************************************
* Adds a school
*
* @param $school Name of school
*
* @return int Status code:
*              0 - Success
*              1 - School name already exists
*              2 - Database error
*******************************************************************************/
function addSchool($school)
{
  // Check that the school name is unique
  if (schoolNameExists($school))
    return 1;
    
  // Add to the database
  $result = mysql_query("INSERT INTO schools (school) VALUES (\"". mysql_real_escape_string($school) . "\")");

  // Check that result is a valid mysql resourse
  if (!$result)
    return 2;
  
  return 0;
}

/*******************************************************************************
* Edits a school
*
* @param $schoolId Id of school
* @param $school Name of school
*
* @return int Status code:
*              0 - Success
*              1 - School id invalid
*              2 - School name already exists
*              3 - Database error
*******************************************************************************/
function editSchool($schoolId, $school)
{
  // Check that school id is valid
  if (!schoolExists($schoolId))
    return 1;
    
  // Check that the school name is unique
  if (schoolNameExists($school, $schoolId))
    return 2;
    
  // Update the database
  $result = mysql_query("UPDATE schools SET school=\"". mysql_real_escape_string($school) . "\" WHERE schoolId=".$schoolId);

  // Check that result is a valid mysql resourse
  if (!$result)
    return 3;
  
  return 0;
}

/*******************************************************************************
* Determines if a school id in the database
*
* @param $schoolId Id of school to search for
*
* @return bool Bool indicating if school exists
*******************************************************************************/
function schoolExists($schoolId)
{
  // Check that id is numeric before query
  if (!is_numeric($schoolId))
    return false;

  $result = mysql_query("SELECT schoolId FROM schools WHERE schoolId={$schoolId} LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Determines if the school name is in the database
*
* @param $school Name of school to search for
* @param $schoolId Id of school to allow
*
* @return bool Bool indicating if school exists
*******************************************************************************/
function schoolNameExists($school, $schoolId = 0)
{
  $result = mysql_query("SELECT schoolId FROM schools WHERE school=\"". mysql_real_escape_string($school) . "\" AND schoolId != {$schoolId} LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Gets grading information for a doclet event
*
* @param $docletId Id of doclet
* @param $userEventId Id of user event
* @param $docletEventId Id of doclet event
* @param $score Score give for the report
* @param $max_score Maximum possible score for the report
*
* @return bool False on error.
*******************************************************************************/
function getGradingInfo($docletId, $userEventId, $docletEventId, &$score, &$max_score)
{
  // Check that ids are numeric before query
  if (!is_numeric($docletId) || !is_numeric($userEventId) || !is_numeric($docletEventId))
    return false;

  // Get score
  $query = "SELECT score FROM docletEvents WHERE docletEventId={$docletEventId} LIMIT 1";
  $result = mysql_query($query);
  if ($result && $row = mysql_fetch_assoc($result))
    $score = (float) $row['score'];

  // Get max score
  $info = getReportInfo($userEventId);
  $assignmentId = $info['assignmentId'];
  $query = "SELECT SUM(maxGrade) as maxGrade FROM assignmentGradeBreakdownsView WHERE assignmentId={$assignmentId} AND docletGradeSectionId IN (SELECT docletGradeSectionId FROM docletGradeSections WHERE docletId={$docletId}) LIMIT 1";
  $result = mysql_query($query);
  if ($result && $row = mysql_fetch_assoc($result))
    $max_score = (float) $row['maxGrade'];

  return true;
}

/*******************************************************************************
* Gets grading information for an assignment
*
* @param $assignmentId Id of assignmet
* @param $gradeSections Array where resulting grade sections will be stored
* @param $gradingParams Array where resulting grading parameters will be stored
*
* @return bool False on error.
*******************************************************************************/
function getAssignmentGradingInfo($assignmentId, &$gradeSections, &$gradingParams)
{
  // Check if assignment id is null
  if ($assignmentId == null)
  {
    // Get grade sections
    $gradeSections = array();
    $query = "SELECT docletGradeSectionId, maxGrade FROM defaultGradeBreakdowns";
    $result = mysql_query($query);
    if ($result)
      while ($row = mysql_fetch_assoc($result))
        $gradeSections[($row['docletGradeSectionId'])] = $row['maxGrade'];

    // Get grade parameters
    $gradingParams = array();
    $query = "SELECT docletGradeParameterId, param FROM defaultGradeParameters";
    $result = mysql_query($query);
    if ($result)
      while ($row = mysql_fetch_assoc($result))
        $gradingParams[($row['docletGradeParameterId'])] = $row['param'];
  }
  else
  {
    // Check that ids are numeric before query
    if (!is_numeric($assignmentId))
      return false;

    // Get grade sections
    $gradeSections = array();
    $query = "SELECT docletGradeSectionId, maxGrade FROM assignmentGradeBreakdownsView WHERE assignmentId={$assignmentId}";
    $result = mysql_query($query);
    if ($result)
      while ($row = mysql_fetch_assoc($result))
        $gradeSections[($row['docletGradeSectionId'])] = $row['maxGrade'];

    // Get grade parameters
    $gradingParams = array();
    $query = "SELECT docletGradeParameterId, param FROM assignmentGradeParametersView WHERE assignmentId={$assignmentId}";
    $result = mysql_query($query);
    if ($result)
      while ($row = mysql_fetch_assoc($result))
        $gradingParams[($row['docletGradeParameterId'])] = $row['param'];
  }

  return true;
}

/*******************************************************************************
* Sets doclet section max score for an assignment
*
* @param $assignmentId Id of assignmet
* @param $docletGradeSectionId Id of doclet grade section
* @param $maxGrade Maximum grade for the section
*
* @return bool False on error.
*******************************************************************************/
function setAssignmentSectionGrade($assignmentId, $docletGradeSectionId, $maxGrade)
{
  // Check that ids are numeric before query
  if (!is_numeric($assignmentId) || !is_numeric($docletGradeSectionId) || !is_numeric($maxGrade))
    return false;

  // Check that grade section exists
  $valid = false;
  $query = "SELECT COUNT(*) FROM docletGradeSections WHERE docletGradeSectionId={$docletGradeSectionId}";
  if (($result = mysql_query($query)) !== false)
    if (mysql_result($result, 0) == 1)
      $valid = true;
  if (!$valid)
    return false;

  $query = "INSERT INTO assignmentGradeBreakdowns (assignmentId, docletGradeSectionId, maxGrade) VALUES ({$assignmentId}, {$docletGradeSectionId}, {$maxGrade}) ON DUPLICATE KEY UPDATE maxGrade={$maxGrade}";

  $result = mysql_query($query);
  return $result;
}

/*******************************************************************************
* Sets doclet section max score for an assignment
*
* @param $assignmentId Id of assignmet
* @param $docletGradeParamId Id of doclet grade section
* @param $value Value for the parameter
*
* @return bool False on error.
*******************************************************************************/
function setAssignmentGradeParameters($assignmentId, $docletGradeParamId, $value)
{
  // Check that ids are numeric before query
  if (!is_numeric($assignmentId) || !is_numeric($docletGradeParamId))
    return false;

  // Check that grading parameter exists
  $valid = false;
  $query = "SELECT COUNT(*) FROM docletGradeParameters WHERE docletGradeParameterId={$docletGradeParamId}";
  if (($result = mysql_query($query)) !== false)
    if (mysql_result($result, 0) == 1)
      $valid = true;
  if (!$valid)
    return false;

  $query = "INSERT INTO assignmentGradeParameters (assignmentId, docletGradeParameterId, param) VALUES ({$assignmentId}, {$docletGradeParamId}, \"{$value}\") ON DUPLICATE KEY UPDATE param=\"{$value}\"";
  $result = mysql_query($query);
  return $result;
}

/*******************************************************************************
* Adds a doclet to the database.  Assumes that string parameters were already
* escaped for MySQL statements.
*
* @param string $name Name of the doclet
* @param string $desc Short description of the doclet
* @param string $javaName Java class name for the doclet
* @param int $docletId Variable where the resulting docletId will be stored
*
* @return int Returns one of the following status codes:
*               0 - Success
*               1 - MySQL error
*******************************************************************************/
function addDoclet($name, $desc, $javaName, &$docletId)
{
  $docletId = false;

  // INSERT into databse
  $sql = "INSERT INTO doclets (docletName, docletDescription, javaName) VALUES (\"{$name}\", \"{$desc}\", \"{$javaName}\")";
  $result = mysql_query($sql);
  if ($result === false)
    return 1;

  // Get auto_increment value
  $docletId = mysql_insert_id();
  if ($docletId === false)
    return 1;

  return 0;
}

/*******************************************************************************
* Adds doclet section with default max score.  Assumes that string parameters
* were already escaped for MySQL statements.
*
* @param int $docletId Id of doclet to add section to
* @param string $name Name of the doclet section
* @param string $desc Short description of the section
* @param float $maxGrade Maximum grade for the section
*
* @return bool False on error.
*******************************************************************************/
function addDocletSection($docletId, $name, $desc, $maxGrade)
{
  // Check that id and grade is numeric before query
  if (!is_numeric($docletId) || !is_numeric($maxGrade))
    return false;

  // Insert section
  $query = "INSERT INTO docletGradeSections (docletId, sectionName, sectionDesc) VALUES ({$docletId}, \"{$name}\", \"{$desc}\")";
  if (($result = mysql_query($query)) == false)
    return false;

  // Get auto_increment value
  if (($sectionId = mysql_insert_id()) === false)
    return 1;

  // Set default
  $query = "INSERT INTO defaultGradeBreakdowns VALUES ({$sectionId}, {$maxGrade})";
  $result = mysql_query($query);
  return $result;
}

/*******************************************************************************
* Adds doclet parameter with default value.  Assumes that string parameters
* were already escaped for MySQL statements.
*
* @param int $docletId Id of doclet to add parameter for
* @param string $name Name of the doclet parameter
* @param string $desc Short description of the parameter
* @param string $value Maximum grade for the section
*
* @return bool False on error.
*******************************************************************************/
function addDocletParam($docletId, $name, $desc, $value)
{
  // Check that id is numeric before query
  if (!is_numeric($docletId))
    return false;

  // Insert parameter
  $query = "INSERT INTO docletGradeParameters (docletId, parameterName, parameterDesc) VALUES ({$docletId}, \"{$name}\", \"{$desc}\")";
  if (($result = mysql_query($query)) == false)
    return false;

  // Get auto_increment value
  if (($paramId = mysql_insert_id()) === false)
    return 1;

  // Set default
  $query = "INSERT INTO defaultGradeParameters VALUES ({$paramId}, \"{$value}\")";
  $result = mysql_query($query);
  return $result;
}

/*******************************************************************************
* Gets statistics on the database
*
* @return mixed Associative array of row counts on success and false otherwise
*******************************************************************************/
function getDbStats()
{
  // Construct query
  $sql = 'SELECT'.
    '(SELECT COUNT(*) FROM users) AS users,'.
    '(SELECT COUNT(*) FROM users WHERE acctType = "professor") AS profs,'.
    '(SELECT COUNT(*) FROM users WHERE acctType = "admin") AS admin,'.
    '(SELECT COUNT(*) FROM schools) AS schools,'.
    '(SELECT COUNT(*) FROM courses) AS courses,'.
    '(SELECT COUNT(*) FROM courses WHERE status="enabled") AS enabledCourses,'.
    '(SELECT COUNT(*) FROM courses WHERE status="disabled") AS disabledCourses,'.
    '(SELECT COUNT(*) FROM assignments) AS assignments,'.
    '(SELECT COUNT(*) FROM doclets) AS doclets,'.
    '(SELECT COUNT(*) FROM enrollments) AS enrollments,'.
    '(SELECT COUNT(*) FROM userEvents) AS reports,'.
    '(SELECT COUNT(*) FROM files) AS files'.
    ' LIMIT 1';

  $result = mysql_query($sql);
  if ($result === false)
    return false;

  // Return associative array
  return mysql_fetch_assoc($result);
}

/*******************************************************************************
* Gets number of students enrolled in the course and the number of reports
* submitted for the course.
*
* @param int $courseId Id of course  
* 
* @return mixed Returns associative array on success, otherwise false.  The 
*               array contains:
*                 numStudent - Number of students
*                 numReports - Number of reports 
*******************************************************************************/
function getCourseStats($courseId)
{
  // Check that the given courseId exists
  if (!courseExists($courseId))
    return false;
    
  // Construct query
  $sql ='SELECT 
  (SELECT COUNT(*) 
   FROM enrollments 
   WHERE courseId='.$courseId.') AS numStudents,
  (SELECT COUNT(*) 
   FROM assignmentEvents
   WHERE assignmentEventId IN (SELECT assignmentEventId 
                               FROM assignments
                               WHERE courseId = '.$courseId.')) AS numReports';
  
  // Make query                             
  $result = mysql_query($sql);

  // Get row    
  if (!$result || ($row = mysql_fetch_assoc($result)) === false)
    return false;
  
  return $row;                               
}

/*******************************************************************************
* Gets the five most active users for the course.
*
* @param int $courseId Id of course
* 
* @return mixed Returns array on success, otherwise false.  The array contains
*                 associative arrays with:
*                   userId - User id
*                   name - User name
*                   numReports - Number of reports by the user 
*******************************************************************************/
function getCourseMostActiveUsers($courseId)
{
  // Check that the given courseId exists
  if (!courseExists($courseId))
    return false;
    
  // Construct query
  $sql ='SELECT u.userId, u.name, 
         COUNT(ue.userEventId) AS numReports 
         FROM users u JOIN userEvents ue ON u.userId = ue.userId AND userEventId IN (SELECT userEventId FROM assignmentEvents WHERE assignmentId IN (SELECT assignmentId FROM assignments WHERE courseId = '.$courseId.')) 
         WHERE u.userId IN (SELECT studentId FROM enrollments WHERE courseId='.$courseId.')
         GROUP BY userId, name
         ORDER BY numReports, ue.dateTime
         LIMIT 5';
  
  // Make query                             
  if (($result = mysql_query($sql)) === false)
    return false;

  // Get rows
  $rtn  = array();    
  while ($row = mysql_fetch_assoc($result))
    $rtn[] = $row;

  return $rtn;
}

/*******************************************************************************
* Adds the user to the current users table. 
*
* @param int $userId Id of user
* @param int $expires Timestamp when the session expires.  If less than current
*                     timestamp, the row is removed from the database 
* 
* @return int Returns one of the following status codes:
*             0 - Success
*             1 - User does not exist
*             2 - Invalid value for $expires             
*             3 - Database error 
*******************************************************************************/
function updateCurrentUser($userId, $expires)
{
  // Check that the given userId exists
  if (!userIdExists($userId))
    return 1;
  
  // Check that expiration time is an integer
  if (!is_int($expires))
    return 2;
  
  // If expiration date has passed, delete user from table
  if ($expires < time())
  {
    $sql = "DELETE
            FROM current_users
            WHERE userId = {$userId}";                           
    if (($result = mysql_query($sql)) === false)
      return 3;
  }
  else
  {
    // Check if the userId is in the table
    $sql = "SELECT
            COUNT(*)
            FROM current_users
            WHERE userId = {$userId}";                             
    if (($result = mysql_query($sql)) === false)
      return 3;
      
    // Determine if this is an update or insert
    if (intval(mysql_result($result, 0, 0)) === 1)
    {
      // Construct query
      $sql = 'UPDATE
              current_users
              SET expires = '.$expires.
              ' WHERE userId = '.$userId;
    }
    else
    {
      // Construct query
      $sql = 'INSERT
              INTO current_users
              (userId, expires)
              VALUES ('.$userId.','.$expires.')';
    }
    
    // Execute query
    if (($result = mysql_query($sql)) === false)
      return 3;
  }

  return 0;
}

/*******************************************************************************
* Removes users from the current users table 
*
* @param int $userId Id of user
* 
* @return int Returns one of the following status codes:
*             0 - Success
*             1 - User does not exist
*             2 - Invalid value for $expires             
*             3 - Database error 
*******************************************************************************/
function removeCurrentUser($userId)
{
  return updateCurrentUser($userId, 0); 
}

/*******************************************************************************
* Removes users for current users table whose session expired 
*******************************************************************************/
function cleanCurrentUsers()
{
  // Construct query
  $sql = 'DELETE FROM current_users WHERE expires < '.time();
  
  // Execute query
  mysql_query($sql);
}

/*******************************************************************************
* Gets number of users currently logged in 
* 
* @return int Number of users or -1 on error  
*******************************************************************************/
function numCurrentUsers()
{
  // Remove old sessions
  cleanCurrentUsers();

  // Construct query
  $sql = 'SELECT COUNT(*) FROM current_users';  
  
  // Execute query
  if (($result = mysql_query($sql)) !== false && ($num = mysql_result($result, 0, 0)) !== false)
    return $num;  
  
  return -1;
}

/*******************************************************************************
* Gets users currently logged in 
* 
* @return mixed Array of users with "name" and "email" or false  
*******************************************************************************/
function getCurrentUsers()
{
  // Remove old sessions
  cleanCurrentUsers();

  // Construct query
  $sql = 'SELECT name, email FROM current_users NATURAL JOIN users';  
  
  // Execute query
  if (($result = mysql_query($sql)) === false)
    return false;  
  
  // Get rows
  $rtn  = array();    
  while ($row = mysql_fetch_assoc($result))
    $rtn[] = $row;

  return $rtn;
}

?>
