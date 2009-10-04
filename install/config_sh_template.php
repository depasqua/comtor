#!/bin/sh

UPLOAD_PATH=<?php echo $_SESSION["paths"]["uploads"]."\n"; ?>

#dir of class files for javadoc
CLASSES=<?php echo $_SESSION["paths"]["code"].DIRECTORY_SEPARATOR."comtor.jar\n"; ?>

CLASSPATH=<?php echo isset($_SESSION["javacp"]) ? $_SESSION["javacp"]."\n" : "\n"; ?>
JAVA_CONFIG=<?php echo $_SESSION["paths"]["private"].DIRECTORY_SEPARATOR."java.properties\n"; ?>

<?php

// Echo aliases
foreach ($_SESSION['aliases'] as $program=>$path)
  echo "alias {$program}='{$path}'\n"; ?> 

?>
