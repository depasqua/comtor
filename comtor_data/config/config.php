<?php
//database host
define("MYSQL_HOST", "localhost");
//database username
define("MYSQL_USERNAME", "username");
//database password for given username
define("MYSQL_PASSWORD", "password");
//name of the database
define("MYSQL_DB", "comtordb");
//SMTP server used for sending mail
define("EMAIL_SMTP_HOST", "smtp.server.com");
//the URL path to your www/ directory of COMTOR
define("URL_PATH", "www.mywebsite.com/comtor/www/");
//the directory path to the comtor_data/uploads/ directory of COMTOR
define("UPLOAD_DIR", "/mywebsite/comtor/comtor_data/uploads/");
//the directory path to the www/ directory of COMTOR
$dir = "/mywebsite/comtor/www/";

define("DEVELOPMENT", false);
define("EMAIL_FROM", "Comment Mentor <no-reply@localhost>");
define("EMAIL_SEND", true);
?>
