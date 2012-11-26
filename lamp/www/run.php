<?php
// Only allow students
$acctTypes = "student";
require_once("loginCheck.php");
?>

<?php
// Connect to database
require_once("connect.php");
require_once("mysqlFunctions.php");

// Set id of current user
$userId = $_SESSION['userId'];

// Directory
require_once("config.php");

// Store IP address of client
$ip = $_SERVER['REMOTE_ADDR'];

// Store information about jar file
$fileSize = $_FILES['file']['size'];
$fileName = str_replace(" ", "", $_FILES['file']['name']);

// Message is used to display errors
$message = "";

// Check to ensure the file is present
if (isset($_FILES['file']) == false OR $_FILES['file']['error'] == UPLOAD_ERR_NO_FILE)
	$message = "You need to upload a jar file!<br>";

// Check file extension
function file_extension($filename) {
	return end(explode(".", $filename));
}

$ext = file_extension($fileName);
if ($ext != "jar")
	$message = "You need to upload a jar file (with a .jar file extension)!<br/>";

// Check that there is an assignment set
if (!isset($_POST['assignmentId']) || ($assignment = getAssignment($_POST['assignmentId'])) === false)
	$message = $message . "You must select an assignment!<br/>";

// Check for assignment options
if (isset($_POST['assignmentId']) && $options = getAssignmentOptions($_POST['assignmentId'])) {
	$_POST['doclet'] = array();
	foreach ($options as $opt)
		if ($doc = getDoclet($opt))
			$_POST['doclet'][] = $doc['javaName'];
}

// Ensure that at least one analyzer was selected
if (isset($_POST['doclet']) == false) {
	$message = $message . "You need to select at least one analyzer!<br/>";
}

// If no errors
if ($message == "") {
	//the tmp_name begins with '/tmp/' so only grab the remaining substring
	$tempFileName = substr($_FILES['file']['tmp_name'], 5);

	mkdir(UPLOAD_DIR . $tempFileName);

	// upload the jar file
	move_uploaded_file($_FILES['file']['tmp_name'], UPLOAD_DIR . $tempFileName . '/' . $fileName);

	// generate the list of doclets selected to execute (the docletList.properties file)
	$myFile = UPLOAD_DIR . $tempFileName . ".txt";
	$fh = fopen($myFile, 'a');

	if (isset($_POST['doclet'])) {
		$doclet = $_POST['doclet'];
		$count = count($doclet);
		$i = 0;

		while ($i < $count) {
			$nextDoclet = "doclet" . ($i+1) . " : " . $doclet[$i] . "\n";
			fwrite($fh, $nextDoclet);
			$i++;
		}
	}
	fclose($fh);

	// starts a script to run javadoc
	$command = $dir . "scripts/javadoc.sh " . $tempFileName . " " . $fileName . " " . $userId . " ". $_POST['assignmentId'];
	exec($command, $output, $return);

	if ($return != 0) {
		switch($return) {
			case 1:
				$_SESSION['msg']['error'] = "Your source code did not compile.  Please resubmit once the code compiles.";
				break;

			case 3:
				$_SESSION['msg']['error'] = "Your jar file did not contain any .java source code file(s).";
				header("Location: submit.php");
				exit;
				break;

			case 2:
				default:
				$_SESSION['msg']['error'] = "There was an error analyzing your code.";
				header("Location: submit.php");
				exit;
				break;
		}
	}

	// Get last report from database
	if (($userEventId = lastReport($_SESSION['userId'])) !== false) {
		$_SESSION['msg']['success'] = "Code submitted sucessfully";

		// Record this as a course submission if course was set and user is in course
		if (isset($_POST['courseId']) && is_numeric($_POST['courseId'])) {

			// Check that user is in the course
			if (isUserInCourse($_SESSION['userId'], $_POST['courseId'])) {

				// recordReportForCourse() checks that the courseId is valid
				if (!recordReportForCourse($userEventId, $_POST['courseId'])) {
					unset($_SESSION['msg']['success']);
					$_SESSION['msg']['error'] = "Error submitting this report for the indicated course.";
				}
			}
			else
			{
				unset($_SESSION['msg']['success']);
				$_SESSION['msg']['error'] = "You cannot submit files for a course you are not enrolled in.";
			}
		}
	
		// Record this as an assignment submission if set and user is in course
		$status = recordReportForAssignment($userId, $userEventId, $_POST['assignmentId']);
	
		switch($status) {
			// Success
			case 0:
				break;
	
			// Invalid variable types
			case 1:
				break;
	
			// Assignment does not exist
			case 2:
				$_SESSION['msg']['error'] = 'Error submitting this report for the indicated assignment.  Assignment does not exist.';
				break;
	
			// User not in course
			case 3:
				$_SESSION['msg']['error'] = 'Error submitting this report for the indicated assignment.  Assignment is not for your course.';
				break;
	
			// Database error
			case 4:
				$_SESSION['msg']['error'] = 'Error submitting this report for the indicated assignment.  Database error.';
				break;
		}
	}
	else
		$_SESSION['msg']['error'] = "Error analyzing submission.";

	// Close database
	mysql_close();
	
	// Don't allow both success and error message to be displayed
	if (isset($_SESSION['msg']['success']) && isset($_SESSION['msg']['error']))
		unset($_SESSION['msg']['success']);
	
	// Redirect to reports page unless last report was not found
	if ($userEventId !== false)
		header("Location: reports.php?userEventId={$userEventId}");
	else
		header("Location: reports.php");
	
	exit();
}
else
{
	$_SESSION['msg']['error'] = $message;
	header("Location: index.php");
	exit;
}

?>
