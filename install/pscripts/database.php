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

 $successful_mysql_connect = @mysql_connect($_POST['MYSQL_HOST'], $_POST['MYSQL_USERNAME'], $_POST['MYSQL_PASSWORD']);
if ($successful_mysql_connect)
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
                
                //      run appropriate migrations!!!
            }

    }
}
else
{
    $errors = 1;
    $errorlist[] = "Failed to connect to database.";
}



?>