databaseUrl = jdbc:mysql://<?php echo $_SESSION["mysql"]["server"] ?>/<?php echo $_SESSION["mysql"]["dbname"] ?> 
username = <?php echo $_SESSION["mysql"]["username"] ?> 
password = <?php echo $_SESSION["mysql"]["password"] ?> 

dictionary = <?php echo $_SESSION["paths"]["resources"].DIRECTORY_SEPARATOR ?>words
mispellings = <?php echo $_SESSION["paths"]["resources"].DIRECTORY_SEPARATOR ?>mispellings
