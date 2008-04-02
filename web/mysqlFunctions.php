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

  $query = "SELECT id FROM masterReports WHERE userID={$userId} GROUP BY dateTime ORDER BY dateTime desc LIMIT 1";
  $result = mysql_query($query);

  $reportId = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $reportId = (int) $row['id'];

  return $reportId;
}

/*******************************************************************************
* Inserts row into database indicating that the given reportId was submitted for
* the given courseId.
* Assumes that the caller has already checked that the reportId and courseId are
* valid for the current user.
* Returns true or false to indicatie if the row was successfully inserted
*******************************************************************************/
function recordReportForCourse($reportId, $courseId)
{
  // Check that reportId and courseId are numeric before query
  if (!is_numeric($reportId) || !is_numeric($courseId))
    return false;

  $query = "INSERT INTO reportProperties (reportId, tableName, propertyId) VALUES ({$reportId}, 'courses', {$courseId})";
  return mysql_query($query);
}

/*******************************************************************************
* Checks if there is already an account for the given e-mail address.
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false.
*******************************************************************************/
function emailExists($email)
{
  $result = mysql_query("SELECT email FROM users WHERE email='$email' LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Inserts a new user into the database
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the user was successfully inserted
*******************************************************************************/
function createNewUser($name, $email, $cryptPassword, $school, $acctType = "student", $enabled = "enabled")
{
  $query = "INSERT INTO users(name, email, password, school, acctType, acctStatus) VALUES ('{$name}', '{$email}', '{$cryptPassword}', '{$school}', '{$acctType}', '{$enabled}')";
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

  $query = "SELECT {$select} FROM users WHERE userID={$userId} LIMIT 1";
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

  $query = "UPDATE users SET validatedDT=NOW(), lastLogin=NOW() WHERE userID={$userId}";
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

  $query = "UPDATE users SET lastLogin=NOW() WHERE userID={$userId}";
  return mysql_query($query);
}

/*******************************************************************************
* Gets all doclets and their info
* Returns false if no doclets are found and an array of associative arrays of
* doclet info if any doclets are found
*******************************************************************************/
function getDoclets()
{
  $query = "SELECT * FROM reports";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $doclets = array();

  while ($row = mysql_fetch_assoc($result))
    array_push($doclets, $row);

  return $doclets;
}

/*******************************************************************************
* Gets all properties for the given subReportId
* Returns false if no properties are found and an array of associative arrays of
* property info if any properties are found
*******************************************************************************/
function getSubReportProperties($subReportId)
{
  // Check that subReportId is numeric before query
  if (!is_numeric($subReportId))
    return false;

  $query = "SELECT * FROM docletReports WHERE reportId={$subReportId} ORDER BY attribute";
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

  $result = mysql_query("SELECT id FROM courses WHERE id = {$courseId} LIMIT 1");

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
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
    $query = "SELECT * FROM courses WHERE id = {$courseId} LIMIT 1";
  else
    $query = "SELECT profId, section, name, semester FROM courses WHERE id = {$courseId} LIMIT 1";

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

  $query = "SELECT name FROM users WHERE userID = {$userId} LIMIT 1";
  $query = "SELECT * FROM users WHERE email='$email' LIMIT 1";
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

  $query = "SELECT courseId FROM roster WHERE courseId = {$courseId} AND studentId = {$userId} LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Gets a list of all reports for the given userId.  If courseId is sent in as a
* parameter and is not false, only the reports for that courseId are returned.
* Returns false if no reports are found. If any are found, returns an array of
* associative arrays (one for each report) with 'id' and 'dateTime'.  The array
* is is order of most recent report to older report.
*******************************************************************************/
function getUserReports($userId, $courseId = false)
{
  // Check that userId and courseId (if set) are numeric before query
  if (!is_numeric($userId) || ($courseId !== false && !is_numeric($courseId)))
    return false;

  $query = "SELECT id, dateTime FROM masterReports WHERE userID={$userId} ORDER BY dateTime DESC";
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
    if ($courseId === false || isReportForCourse($row['id'], $courseId))
      array_push($reports, $row);
  }

  return $reports;
}

/*******************************************************************************
* Gets a list of all reports for the given courseId.
* Returns false if no reports are found. If any are found, returns an array of
* associative arrays (one for each report) with 'id' and 'dateTime'.  The array
* is is order of most recent report to older report.
*******************************************************************************/
function getUserReportsByCourse($courseId)
{
  // Check that courseId is numeric before query
  if (!is_numeric($courseId))
    return false;

  $query = "SELECT reportId FROM reportProperties WHERE tableName = 'courses' AND propertyId = {$courseId}";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // reports found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  $reports = array();

  while ($row = mysql_fetch_assoc($result))
    array_push($reports, $row);

  return $reports;
}


/*******************************************************************************
* Check if the given main reportId was submitted for the given courseId
* Returns true or false
*******************************************************************************/
function isReportForCourse($reportId, $courseId)
{
  // Check that reportId and courseId are numeric before query
  if (!is_numeric($reportId) || !is_numeric($courseId))
    return false;

  $query = "SELECT reportId FROM reportProperties WHERE reportId = {$reportId} AND tableName = 'courses' AND propertyId = {$courseId} LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Check if the given main reportId was submitted by the given userId
* Returns true or false
*******************************************************************************/
function isReportForUser($reportId, $userId)
{
  // Check that reportId and userId are numeric before query
  if (!is_numeric($reportId) || !is_numeric($userId))
    return false;

  $query = "SELECT id FROM masterReports WHERE id={$reportId} AND userId={$userId} LIMIT 1";
  $result = mysql_query($query);

  // Check that result is a valid mysql resourse
  if (!$result)
    return false;

  return (mysql_num_rows($result) > 0);
}

/*******************************************************************************
* Gets an associative array of all information the given reportId.
* Returns false if no files are found. If any are found, returns an array of
* associative arrays (one for each file).
*******************************************************************************/
function getReportInfo($reportId)
{
  // Check that reportId is numeric before query
  if (!is_numeric($reportId))
    return false;

  $query = "SELECT * FROM masterReports WHERE id={$reportId} LIMIT 1";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // reports found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  return mysql_fetch_assoc($result);
}

/*******************************************************************************
* Gets an array of all file for the given reportId.
* Returns false if no files are found. If any are found, returns an array of
* associative arrays (one for each file).
*******************************************************************************/
function getReportFiles($reportId)
{
  // Check that reportId is numeric before query
  if (!is_numeric($reportId))
    return false;

  $query = "SELECT filename, contents FROM files WHERE reportId={$reportId}";
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
* Gets id identifying a given doclets portion of the given reportId
* Returns false if no report is found, otherwise the id.
*******************************************************************************/
function getSubReportId($docletId, $reportId)
{
  // Check that docletId and reportId are numeric before query
  if (!is_numeric($docletId) || !is_numeric($reportId))
    return false;

  $query = "SELECT id FROM masterDoclets WHERE masterReportId={$reportId} AND docletReportId={$docletId} LIMIT 1";
  $result = mysql_query($query);

  $id = false;

  if ($result && $row = mysql_fetch_assoc($result))
    $id = (int) $row['id'];

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
        $where .= "OR masterReportId={$report['id']} ";
    }
  }

  // Find all main reports for a course
  else if ($courseId !== false)
  {
    $reports = getCourseReports($courseId);

    // Add to WHERE clause using OR, it will later be removed from the first one
    foreach ($reports as $report)
      $where .= "OR masterReportId={$report['id']} ";
  }

  // Fix up the where clause
  if ($where != "")
  {
    // This is going after another WHERE condition so add AND
    // Remove first OR and add parenthesis
    $where = " AND (" . substr($where, 3) . ")";
  }

  $query = "SELECT COUNT(*) FROM masterDoclets WHERE docletReportId={$docletId}{$where}";
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

  $result = mysql_query("SELECT password FROM users WHERE userID=$userId");

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

  $query = "UPDATE users SET password='{$password}', passwordChangeDT=NOW() WHERE userID={$userId}";
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

  $query = "UPDATE users SET acctStatus='disabled' WHERE userID={$userId}";
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
* Gets all users and the info provided in the fields parameter.
* fields is a array of column names in the database that are to be retrieved.
* enabled can be 'enabled', 'disabled', or 'all'.
* Returns an array of associative arrays of user info
*******************************************************************************/
function getUsers($fields = array(), $enabled = "all", $acctType = "all")
{
  // Create WHERE clause for enabled
  $where = "";
  if ($enabled == "enabled")
    $where = " WHERE acctStatus='enabled'";
  else if ($enabled == "disabled")
    $where = " WHERE acctStatus='disabled'";

  if ($acctType != all)
  {
    // Check that the given acctType is a valid column value
    $validValues = getEnumValues("users", "acctType");
    if (in_array($acctType, $validValues))
      if ($where == "")
        $where = " WHERE acctType='{$acctType}'";
      else
        $where .= " AND acctType='{$acctType}'";
  }

  // Create column selection text
  if (($columns = makeColumnStr($fields, "users")) === false)
    return false;

  $query = "SELECT {$columns} FROM users{$where}";
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
* Deletes the account for given userId
* TODO: Delete all other database entries for the user
* Returns true or false to indicate if the user was successfully deleted
*******************************************************************************/
function deleteUser($userId)
{
  // Check that userId is numeric before query
  if (!is_numeric($userId))
    return false;

  $query = "DELETE FROM users WHERE userID={$userId} LIMIT 1";
  return mysql_query($query);
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

  $query = "UPDATE users SET acctStatus='enabled' WHERE userID={$userId} LIMIT 1";
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

  $query = "UPDATE users SET acctType = '{$acctType}' WHERE userID={$userId} LIMIT 1";
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

  $query = "SELECT acctType FROM users WHERE userID={$userId} LIMIT 1";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  return mysql_result($result, 0);
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

  $query = "SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME = '{$table}' AND TABLE_SCHEMA = 'comtor' AND COLUMN_NAME = '{$column}'";
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
* Deletes the given courseId
* TODO: Delete all other database entries for the course
* Returns true or false to indicate if the course was successfully deleted
*******************************************************************************/
function deleteCourse($courseId)
{
  // Check that courseId is numeric before query
  if (!is_numeric($courseId))
    return false;

  $query = "DELETE FROM courses WHERE id={$courseId} LIMIT 1";
  return mysql_query($query);
}

/*******************************************************************************
* Updates data for the given courseId
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the user was successfully inserted
*******************************************************************************/
function updateCourseInfo($courseId, $profId, $name, $section, $semester, $comment)
{
  // Check that courseId and profId are numeric before query
  if (!is_numeric($courseId) || !is_numeric($profId))
    return false;

  $query = "UPDATE courses SET profId = $profId, name = '{$name}', section = '{$section}', semester = '{$semester}', comment = '{$comment}' WHERE id = '{$courseId}'";
  return mysql_query($query);
}

/*******************************************************************************
* Creates new course with the given parameters
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the user was successfully inserted
*******************************************************************************/
function addNewCourse($profId, $name, $section, $semester, $comment)
{
  // Check that profId is numeric before query
  if (!is_numeric($profId))
    return false;

  $query = "INSERT INTO courses (profId, name, section, semester, comment) VALUES ({$profId}, '{$name}', '{$section}', '{$semester}', '{$comment}')";
  return mysql_query($query);
}

/*******************************************************************************
* Enrolls the given userId in the given course
* Assumes that the caller has already checked that the parameters are valid
* Returns true or false to indicate if the user was successfully inserted
*******************************************************************************/
function courseEnroll($userId, $courseId)
{
  // Check that userId and courseId are numeric before query
  if (!is_numeric($userId) || !is_numeric($courseId))
    return false;

  $query = "INSERT INTO roster (courseId, studentId) VALUES ({$courseId}, {$userId})";
  return mysql_query($query);
}


/*******************************************************************************
* Gets the courses that the given userId is enrolled in if userId is set.
* Otherwise, it gets all courses.
* Returns an array of arrays with the arrays having indices 'id', 'profId',
* 'section', 'name', 'semester', and 'comment' for each course or false if
* userId is invalid or no courses are found
*******************************************************************************/
function getCourses($userId = false)
{
  $rtn = array();

  // Find courses for the user
  if ($userId !== false)
  {
    // Check that userId is numeric before query
    if (!is_numeric($userId))
      return false;
    $query = "SELECT courseId FROM roster WHERE studentId = {$userId}";

    if (($result = mysql_query($query)) === false)
      return false;

    // Go through each id and get information about the course
    while ($course = mysql_fetch_assoc($result))
    {
      $query = "SELECT id, profId, section, name, semester, comment FROM courses WHERE id = {$course['courseId']} LIMIT 1";
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
    $query = "SELECT * FROM courses";
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

  return $rtn;
}


/*******************************************************************************
* Gets all students in the course.
* Returns an array of arrays with the arrays having indices 'userID', 'name',
* and 'email' for each student
*******************************************************************************/
function getCourseStudents($courseId)
{
  $rtn = array();

  // Find course ids for the user
  $query = "SELECT studentId FROM roster WHERE courseId = {$courseId}";
  $result = mysql_query($query);

  // Checks that result is a valid mysql resource and also that there were
  // doclets found before proceeding.  Uses short-circuit evaluation!
  if ($result === false || mysql_num_rows($result) == 0)
    return false;

  // Go through each id and get information about the student
  while ($id = mysql_fetch_assoc($result))
  {
    $query = "SELECT userID, name, email FROM users WHERE userID = {$id['studentId']} LIMIT 1";
    if ($userResult = mysql_query($query))
      if ($row = mysql_fetch_assoc($userResult))
        array_push($rtn, $row);
  }

  return $rtn;
}

/*******************************************************************************
* Gets the courses that the given profId is teaching.  $connected is used to
* indicate whether there is already a MySQL connection to use.
* Returns an array of arrays with the arrays having indices 'id', 'section',
* 'name', 'semester' and 'comment' for each course
*******************************************************************************/
function getProfCourses($profId, $connected = false)
{
  $rtn = array();

  // Find courses for the professor
  $query = "SELECT id, section, name, semester, comment FROM courses WHERE profId = {$profId} ORDER BY section ASC";
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

?>
