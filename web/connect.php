<?php
  require_once("config.php");
  require_once("mysqlFunctions.php");
  mysql_connect(MYSQL_HOST, MYSQL_USERNAME, MYSQL_PASSWORD);
  mysql_select_db(MYSQL_DB);
?>
