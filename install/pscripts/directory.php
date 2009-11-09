<?php

if (empty($_POST['URL_PATH']))
{
    $errors = true;
    $errorlist[] = "You must specify a URL path!";
}

?>