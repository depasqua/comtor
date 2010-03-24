<?php
//The database host.
//Example: "localhost"
define("MYSQL_HOST", "localhost");

//The database username.
//Example: "root"
define("MYSQL_USERNAME", "username");

//The database password for the given username.
//Example: "mypassword"
define("MYSQL_PASSWORD", "password");

//The name of the database that COMTOR will use.
//Example: "comtordb"
define("MYSQL_DB", "dbname");

//The SMTP server used for sending mail.
define("EMAIL_SMTP_HOST", "smtp.server.com");

//the URL path to your www/ directory of COMTOR
//Please note that there is no "/" at the end of this string.
//Example: "localhost/~yourname/comtor/www"
define("URL_PATH", "www.mywebsite.com/comtor/www");

//the directory path to the comtor_data/uploads/ directory of COMTOR
//This will be the absolute path that your directory is in, which means
//that it must start with the base directory "/".
//Please note that at the end of this string, there must be another "/".
//Example: "/home/yourname/public_html/comtor/comtor_data/uploads/"
define("UPLOAD_DIR", "/mywebsite/comtor/comtor_data/uploads/");

//the directory path to the www/ directory of COMTOR
//Example: /home/yourname/public_html/comtor/www/
//Please note that there is a "/" at the beginning and end of this string.
$dir = "/mywebsite/comtor/www/";

//Please do not touch the variables below.
define("DEVELOPMENT", false);
define("EMAIL_FROM", "Comment Mentor <no-reply@localhost>");
define("EMAIL_SEND", true);
?>
