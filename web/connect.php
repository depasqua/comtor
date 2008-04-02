<?php
  require_once("directory.php");
  require_once(PRIVATE_DIR."config.php");
  require_once("mysqlFunctions.php");
  mysql_connect(MYSQL_HOST, MYSQL_USERNAME, MYSQL_PASSWORD);
  mysql_select_db('comtor');
?>
