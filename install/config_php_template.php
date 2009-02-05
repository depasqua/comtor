<?php echo "<?php"; ?>

  define("MYSQL_HOST", "<?php echo $_SESSION["mysql"]["server"] ?>");
  define("MYSQL_USERNAME", "<?php echo $_SESSION["mysql"]["username"] ?>");
  define("MYSQL_PASSWORD", "<?php echo $_SESSION["mysql"]["password"] ?>");
  define("MYSQL_DB", "<?php echo $_SESSION["mysql"]["dbname"] ?>");

  // E-Mail Settings
  define('EMAIL_FROM', 'Comment Mentor <<?php echo $_SESSION["admin"]["email"] ?>>');
  define('EMAIL_SMTP_HOST', '<?php echo $_SESSION["smtp"] ?>');
  define('EMAIL_SEND', true);

  $dir = "<?php echo $_SESSION["paths"]["www"].DIRECTORY_SEPARATOR ?>";
  define("URL_PATH", "<?php echo $_SESSION["url"] ?>");
  define("UPLOAD_DIR", "<?php echo $_SESSION["paths"]["uploads"].DIRECTORY_SEPARATOR ?>");

  define("DEVELOPMENT", false);

<?php echo "?>"; ?>
