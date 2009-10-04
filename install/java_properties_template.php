databaseUrl = jdbc:mysql://<?php echo $_SESSION["mysql"]["server"]. "/" . $_SESSION["mysql"]["dbname"]."\n"; ?>
username = <?php echo $_SESSION["mysql"]["username"]."\n"; ?>
password = <?php echo $_SESSION["mysql"]["password"]."\n"; ?>

dictionary = <?php echo $_SESSION["paths"]["resources"].DIRECTORY_SEPARATOR ?>words
mispellings = <?php echo $_SESSION["paths"]["resources"].DIRECTORY_SEPARATOR ?>mispellings
