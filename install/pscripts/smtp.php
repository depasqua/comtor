<?php

if (empty($_POST['EMAIL_SMTP_HOST']))
{
    $errors = true;
    $errorlist[] = "You must include a value for SMTP Hostname!";
}


?>