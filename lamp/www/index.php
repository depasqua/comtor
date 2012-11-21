<?php

require_once("config.php");

session_start();

//check for session id
if(!isset($_SESSION['userId'])) {
	header("Location: http://" . URL_PATH . "/loginForm.php");
	exit;
}

require_once("smarty/Smarty.class.php");

$tpl = new Smarty();

require_once("header1.php");

// If professor, get stats on each course
if ($_SESSION['acctType'] == "professor" && isset($userCourses) && is_array($userCourses)) {
	foreach ($userCourses as &$course) {
		// Get course statistics
		$arr = getCourseStats($course['courseId']);
		
		if ($arr != false)
		$course = array_merge($course, $arr);
		
		// Get most active students
		$course['mostActiveStudents'] = getCourseMostActiveUsers($course['courseId']);
	}
	unset ($course);

	// Reassign courses to template
	$tpl->assign('courses', $userCourses);
}

if ($_SESSION['acctType'] == "student")
	$tpl->assign("url", "dropbox.php");
  
if ($_SESSION['acctType'] == "admin") { 
	// Get number of requests
	$requests = getNumPendingRequests();
	if ($requests !== false)
		$tpl->assign('requests', $requests);
}

// Fetch template
$tpldata = $tpl->fetch("welcome.tpl");

// Assign breadcrumbs
$breadcrumbs = array();
$breadcrumbs[] = array('text' => 'Home', 'href' => 'index.php');
$tpl->assign('breadcrumbs', $breadcrumbs);

// Assign tooltips
$tooltips = file_get_contents('tooltips/index.html');
$tpl->assign('tooltips', $tooltips);

// Assign page html
$tpl->assign('tpldata', $tpldata);

// Display template
$tpl->display("htmlmain.tpl");

?>
