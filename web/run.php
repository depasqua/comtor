<?php
  // Only allow students
  $acctTypes = "student";
  require_once("loginCheck.php");
?>
<?php

//set id of current user
$userId = $_SESSION['userId'];

//directory
include("directory.php");

//store IP address of host
$ip = $_SERVER['REMOTE_ADDR'];

//store information about jar file
$fileSize = $_FILES['file']['size'];
$fileName = str_replace(" ", "", $_FILES['file']['name']);

//message is used to display errors
$message = "";

//check to make sure the file is present
if(isset($_FILES['file']) == false OR $_FILES['file']['error'] == UPLOAD_ERR_NO_FILE){
  $message = "You need to upload a jar file!<br>";
}

//check file extension
function file_extension($filename)
{
    return end(explode(".", $filename));
}
$ext = file_extension($fileName);
if($ext != jar){
  $message = "You need to upload a jar file!<br>";
}

//check to make sure at least one doclet was selected
if(isset($_POST['doclet']) == false){
  $message = $message . "You need to select at least one analyzer!";
}

//if no errors
if($message == "")
{
  //the tmp_name begins with '/tmp/' so only grab the remaining substring
  $tempFileName = substr($_FILES['file']['tmp_name'], 5);

  //upload the jar file
  move_uploaded_file($_FILES['file']['tmp_name'], UPLOAD_DIR . $fileName);

  //generate list of doclets selected (Doclets.txt)
  $myFile = UPLOAD_DIR . $tempFileName . ".txt";
  $fh = fopen($myFile, 'a');

  if(isset($_POST['doclet'])){
    $doclet = $_POST['doclet'];
    $count = count($doclet);
    $i = 0;

    while ($i < $count){
      $nextDoclet = $doclet[$i] . "\n";
      fwrite($fh, $nextDoclet);
      $i++;
    }
  }
  fclose($fh);

  //starts a script to run javadoc
  exec($dir . "scripts/javadoc.sh " . $tempFileName . " " . $fileName . " " . $userId, $output);

  // Determine time of this report
  // Connect to database
  include("connect.php");

  // Get last report from database
  if (($userEventId = lastReport($_SESSION['userId'])) !== false)
  {
    $_SESSION['msg']['success'] = "Code submitted sucessfully";
    // Record this as a course submission if course was set and user is in course
    if (isset($_POST['courseId']) && is_numeric($_POST['courseId']))
    {
      // Check that user is in the course
      if (isUserInCourse($_SESSION['userId'], $_POST['courseId']))
      {
        // recordReportForCourse() checks that the courseId is valid
        if (recordReportForCourse($userEventId, $_POST['courseId']))
        {
        }
        else
        {
          unset($_SESSION['msg']['success']);
          $_SESSION['msg']['error'] = "Error submitting this report for the indicated course.";
        }
      }
      else
      {
        unset($_SESSION['msg']['success']);
        $_SESSION['msg']['error'] = "You cannot submit files for a course you are no enrolled in.";
      }
    }
  }
  else
  {
    $_SESSION['msg']['error'] = "Error analyzing code.";
  }

  // Close database
  mysql_close();

  // Redirect to reports page unless last report was not found
  if ($userEventId !== false)
    header("Location: reports.php?userEventId={$userEventId}");
  else
    header("Location: reports.php");

  exit();
}
?>
<?php include_once("header.php"); ?>

<table>
 <tr>
  <td align="center">
  <? echo $message; ?><br><a href="index.php">Go back</a>.
  </td>
 </tr>
</table>

<?php include_once("footer.php"); ?>
