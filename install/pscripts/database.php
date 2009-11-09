<?php 
/*
$errors = 1;
$errorlist[] = "Validation failed!";
//$errortext = "Validation failed!";
*/
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


// try connecting to the db
// if (connect succeeded)
//      determine current version
//      run appropriate migrations!!!
// if not
//      throw error : (

?>