<?
session_start();
session_destroy();

//redirect to comment mentor
header("Location: http://csjava/~brigand2/index.php");
exit;
?>