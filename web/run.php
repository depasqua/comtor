<?php require_once("loginCheck.php"); ?>
<?php

//set id of current user
$userID = $_SESSION['userID'];

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
  move_uploaded_file($_FILES['file']['tmp_name'], $dir . $fileName);

  //generate list of doclets selected (Doclets.txt)
  $myFile = $dir . $tempFileName . ".txt";
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
  exec($dir . "scripts/javadoc.sh " . $tempFileName . " " . $fileName . " " . $userID);


  // Determine time of this report
  // Connect to database
  include("connect.php");

  // Get last report from database
  if (($reportId = lastReport($_SESSION['userID'])) !== false)
  {
    // Record this as a course submission if course was set
    if (isset($_POST['course']) && is_numeric($_POST['course']))
    {
      if (recordReportForCourse($reportId, $_POST['course']))
      {
      }
      else
      {
        $_SESSION['msg']['error'] = "Error submitting this report for the indicated course.";
      }
    }
  }

  // Close database
  mysql_close();

  // Redirect to reports page
  header("Location: reports.php?id={$reportId}");
  exit;
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
