<?php 

if (empty($_POST['MYSQL_HOST']))
{
    $errors = true;
    $errorlist[] = "You must specify a Database Server!";
}

if (empty($_POST['MYSQL_USERNAME']))
{
    $errors = true;
    $errorlist[] = "You must specify a Username!";
}

if (empty($_POST['MYSQL_PASSWORD']))
{
    $errors = true;
    $errorlist[] = "You must specify a Password!";
}

if (empty($_POST['MYSQL_DB']))
{
    $errors = true;
    $errorlist[] = "You must specify a Database Name!";
}
if (!$errors)
 $con = @mysql_connect($_POST['MYSQL_HOST'], $_POST['MYSQL_USERNAME'], $_POST['MYSQL_PASSWORD']);
if ($con)
{
    if (!mysql_select_db($_POST['MYSQL_DB']))
        {
            $errors = 1;
            $errorlist[] = "Database \"{$_POST['MYSQL_DB']}\" does not exist or you do not have permission to access it.  Please make sure the database exists and that the username is granted all privileges on the database.";
        }
    else
    {
         // Check that there are no tables in the database
            $result = mysql_query("SHOW TABLES");
            if ((!$result || mysql_num_rows($result)) && !isset($_SESSION['dbempty']) && !$_SESSION['upgrade'])
              {
                  $errors = true;
                  $errorlist[] = "Database already contains tables.  Please drop all tables in database \"{$_POST['MYSQL_DB']}\" for a fresh install.";
              }
            else {
                //      determine current version
                
                $versionq = "select * from schema_version";
                $result = mysql_query($versionq);
                if (@mysql_num_rows($result)>0)
                {
                    // if we found a version in the DB
                    $item = mysql_fetch_array($result);
                    $current_db_version = $item['version'];
                    
                    // determine which migrations need to run
                    $found_version = false;
                    foreach ($migration_releases as $migration_release)
                    {
                        if ($found_version) // if this release is after the current db version release
                            $migration_releases_to_run[] = $migration_release; // add the migration to this release to the queue
                        if ($migration_release == $current_db_version)
                            $found_version = true;
                    }
                    
                }
                else // if we couldn't find a version in the database, we don't know what migrations to run!
                {
                    $errors = 1;
                    $errorlist[] = "Could not determine current database schema version.";
                }
                
                
                // load in list of releases
                // require_once('../../migrations/releases.php');
                
                // Constants

                if (is_array($migration_releases_to_run))
                foreach ($migration_releases_to_run as $migration_release)
                {
                    echo "Migrating Database to ".$migration_release."<br />";
                    if ($migration_release == "1.0")
                    {
                // START 1.0 MIGRATION CODE
                      define("SCHEMA_PATH", "../migrations/1.0/db/schema.sql");
                      define("MYSQL_DATA_PATH", escapeshellarg("../migrations/1.0/db/data.sql"));

                              // Create new schema with view definers changed
                              $host = ($_POST['MYSQL_HOST'] == "localhost") ? "localhost" : escapeshellarg($_POST['MYSQL_HOST']);
                              $tmpFilename = tempnam(sys_get_temp_dir(), "SQL");
                              if (($fh_in = @fopen(SCHEMA_PATH, "r")) && ($fh_out = @fopen($tmpFilename, "w")))
                              {                 
                                while ($line = fgets($fh_in))
                                {
                                  $serverName = ($_POST['MYSQL_HOST'] == "localhost") ? "localhost" : $_SERVER['SERVER_NAME'];
                                  $line = str_replace("`comtor`@`localhost`", "`{$_POST['MYSQL_USERNAME']}`@`{$serverName}`", $line);
                                  fwrite($fh_out, $line);
                                }                   
                                fclose($fh_in);
                                fclose($fh_out);

                                // Import the database schema
                                $hostOpt = ($host == "localhost") ? "" : "-h " . $host;  
                                $cmd = sprintf("mysql %s -u %s -p%s %s < %s 2>&1", $hostOpt, escapeshellarg($_POST['MYSQL_USERNAME']), escapeshellarg($_POST['MYSQL_PASSWORD']), escapeshellarg($_POST['MYSQL_DB']), escapeshellarg($tmpFilename));
                                exec($cmd, $output, $rtn);
                                unlink($tmpFilename);
                                if ($rtn != 0)
                                  {
                                      $errors = 1;
                                      $errorlist[] = "Error importing schema. MySQL Output:<br/>" . nl2br(implode("<br/>", $output)); 
                                  }
                                else if (!empty($output))
                                  {
                                      $errors = 1;
                                      $errorlist[] = "Error importing schema. MySQL Output:<br/>" . nl2br(implode("<br/>", $output));
                                  }
                                else
                                {
                                  // Import the database data
                                  $cmd = sprintf("mysql %s -u %s -p%s %s < %s 2>&1", $hostOpt, escapeshellarg($_POST['MYSQL_USERNAME']), escapeshellarg($_POST['MYSQL_PASSWORD']), escapeshellarg($_POST['MYSQL_DB']), MYSQL_DATA_PATH);
                                  exec($cmd, $output);
                                  if (!empty($output))
                                    {
                                        $errors = 1;
                                        $errorlist[] = "Error importing required data. MySQL Output:<br/>" . nl2br(implode("<br/>", $output));
                                    }

                                  // Store information in session
                                  $_SESSION["mysql"] = array(
                                    "server"=>$_POST['MYSQL_HOST'],
                                    "username"=>$_POST['MYSQL_USERNAME'],
                                    "password"=>$_POST['MYSQL_PASSWORD'],
                                    "dbname"=>$_POST['MYSQL_DB']
                                  );

                                }
                              }
                              else
                                {
                                    $errors = true;
                                    $errorlist[] = "Error importing schema.<br/>";
                                }
                    }
                    // END 1.0 MIGRATION CODE
                    else 
                    {
                        // run a normal migration
                        $migrate_path = "../migrations/".$migration_release."/db/migrate.sql";
                        $host = ($_POST['MYSQL_HOST'] == "localhost") ? "localhost" : escapeshellarg($_POST['MYSQL_HOST']);
                        
                        $hostOpt = ($host == "localhost") ? "" : "-h " . $host;  
                        $cmd = sprintf("mysql %s -u %s -p%s %s < %s 2>&1", $hostOpt, escapeshellarg($_POST['MYSQL_USERNAME']), escapeshellarg($_POST['MYSQL_PASSWORD']), escapeshellarg($_POST['MYSQL_DB']), escapeshellarg($migrate_path));
                        exec($cmd, $output, $rtn);
                        if ($rtn != 0)
                          {
                              $errors = 1;
                              $errorlist[] = "Error running migration to ".$migration_release.". MySQL Output:<br/>" . nl2br(implode("<br/>", $output)); 
                          }
                        else if (!empty($output))
                          {
                              $errors = 1;
                              $errorlist[] = "Error running migration to ".$migration_release.". MySQL Output:<br/>" . nl2br(implode("<br/>", $output));
                          }
                    }
                }
                if (!$errors)
                {
                    // If there were no errors running all the migrations, go to the next step!
                    $step++;
                }
                            
                            
                        }
                        
                      }
mysql_close($con);
}
else
{
    $errors = 1;
    $errorlist[] = "Failed to connect to database.";
}



?>