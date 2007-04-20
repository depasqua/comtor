<?
session_start();
$userID = $_SESSION['userID'];

$dir = "/home/brigand2/public_html/";

//store IP address of host
$ip = $_SERVER['REMOTE_ADDR'];

//store info about jar file
$fileSize = $_FILES['file']['size'];
$fileName = str_replace(" ", "", $_FILES['file']['name']);

//check to make sure the file is present
if(isset($_FILES['file']) == false OR $_FILES['file']['error'] == UPLOAD_ERR_NO_FILE){
	die('--No files--');
}

//check for upload errors
if($_FILES['file']['error'] != UPLOAD_ERR_OK){
    die('--Error occured during upload--');
}

//check to make sure it's a jar file
function file_extension($filename)
{
    return end(explode(".", $filename));
}
$ext = file_extension($fileName);
if($ext != jar){
	die('--You did not upload a jar file--');
}

//check to make sure at least one doclet was selected
if(isset($_POST['doclet']) == false){
	die('--No doclets were selected--');
}
	
//the tmp_name begins with '/tmp/' so only grab the remaining substring
$tempFileName = substr($_FILES['file']['tmp_name'], 5);

//upload the jar file
move_uploaded_file($_FILES['file']['tmp_name'], $dir . $fileName);

//generate Doclets.txt for the temp dir
$myFile = $dir . $tempFileName . ".txt";
$fh = fopen($myFile, 'a') or die("can't open file");

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
exec($dir . "javadoc.sh " . $tempFileName . " " . $fileName . " " . $userID);

//display report
header('Content-Type: text/html; charset=ISO-8859-1');
readfile($dir . $tempFileName . '/ComtorReport.php');
?>