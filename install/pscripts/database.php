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
    //      determine current version
    //      run appropriate migrations!!!
}
else
{
    $errors = 1;
    $errorlist[] = "Failed to connect to database.";
}



?>