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
            if ((!$result || mysql_num_rows($result)) && !isset($_SESSION['dbempty']))
              {
                  $errors = true;
                  $errorlist[] = "Database already contains tables.  Please drop all tables in database \"{$_POST['MYSQL_DB']}\".";
              }
            else {
                //      determine current version
                
                // load in list of releases
                // require_once('../../migrations/releases.php');
                
                // Constants
                // START 1.0 MIGRATION CODE
                if ($migration_release == "1.0")
                {
                  define("SCHEMA_PATH", "../migrations/1.0/db/schema.sql");
                  define("MYSQL_DATA_PATH", escapeshellarg("../migrations/1.0/db/data.sql"));

                          // Create new schema with view definers changed
                          $host = ($_POST['server'] == "localhost") ? "localhost" : escapeshellarg($_POST['server']);
                          $tmpFilename = tempnam(sys_get_temp_dir(), "SQL");
                          if (($fh_in = @fopen(SCHEMA_PATH, "r")) && ($fh_out = @fopen($tmpFilename, "w")))
                          {                 
                            while ($line = fgets($fh_in))
                            {
                              $serverName = ($_POST['server'] == "localhost") ? "localhost" : $_SERVER['SERVER_NAME'];
                              $line = str_replace("`comtor`@`localhost`", "`{$_POST['username']}`@`{$serverName}`", $line);
                              fwrite($fh_out, $line);
                            }                   
                            fclose($fh_in);
                            fclose($fh_out);

                            // Import the database schema
                            $hostOpt = ($host == "localhost") ? "" : "-h " . $host;  
                            $cmd = sprintf("mysql %s -u %s -p%s %s < %s 2>&1", $hostOpt, escapeshellarg($_POST['username']), escapeshellarg($_POST['password']), escapeshellarg($_POST['dbname']), escapeshellarg($tmpFilename));
                            exec($cmd, $output, $rtn);
                            unlink($tmpFilename);
                            if ($rtn != 0)
                              $error = "Error importing schema. MySQL Output:<br/>" . nl2br(implode("<br/>", $output)); 
                            else if (!empty($output))
                              $error = "Error importing schema. MySQL Output:<br/>" . nl2br(implode("<br/>", $output));
                            else
                            {
                              // Import the database data
                              $cmd = sprintf("mysql %s -u %s -p%s %s < %s 2>&1", $hostOpt, escapeshellarg($_POST['username']), escapeshellarg($_POST['password']), escapeshellarg($_POST['dbname']), MYSQL_DATA_PATH);
                              exec($cmd, $output);
                              if (!empty($output))
                                $error = "Error importing required data. MySQL Output:<br/>" . nl2br(implode("<br/>", $output));

                              // Store information in session
                              $_SESSION["mysql"] = array(
                                "server"=>$_POST['server'],
                                "username"=>$_POST['username'],
                                "password"=>$_POST['password'],
                                "dbname"=>$_POST['dbname']
                              );

                              // Increment step
                              $step++;
                            }
                          }
                          else
                            $error = "Error importing schema.<br/>";
                }
                // END 1.0 MIGRATION CODE
                else 
                {
                    // run a normal migration
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