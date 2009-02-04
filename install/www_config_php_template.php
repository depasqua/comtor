<?php echo "<?php"; ?>

  // Define private directory where config.php is
  define("PRIVATE_DIR", "<?php echo $_SESSION["paths"]["private"].DIRECTORY_SEPARATOR; ?>");

  require_once(PRIVATE_DIR."config.php");

<?php echo "?>"; ?>
