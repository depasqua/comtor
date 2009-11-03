<?php


session_start();
if (!isset($_SESSION['currentstep']))
    $_SESSION['currentstep'] = 0;

if ($_GET['reset'] == "1")
{
    session_destroy();
    session_start();
}

include("../comtor_data/config/config.php");
$handle = fopen("../migrations/1.2/config/configspec.txt", "r");

while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
    $num = count($data);
    if (substr($data[0], 0, 1) == "[") // if the line starts with "[", then it's a new section
    {
        if (isset($this_section))
            {
                $sections[] = $this_section;
                unset($this_section);
            }
        $slug = substr($data[0], 1, strlen($data[0])-2);
        $this_section[0] = $slug;
        // echo "section <b>".substr($data[0], 1, strlen($data[0])-2)."</b><br />";
        $titlenext = true;
    }
    elseif ($titlenext)
    {
        $this_section[2] .= "<h1>".$data[0]."</h1>\n";
        $this_section[1] = $data[0];
        $titlenext = false;
        $descnext = true;
    }
    elseif ($descnext)
    {
        $this_section[2] .= $data[0]."\n";
        $descnext = false;
    }
    elseif ($data[0])
    {
        //echo "<p> $num fields in line $row: <br /></p>\n";
        $name = $data[0];
        $datatype = $data[1];
        $default = $data[2];
        $prompt = $data[3];
        $required = ($data[4] == 'r')? true : false;
        
        $this_section[2] .= "\n".$prompt;
        
        if ($datatype == "string" || $datatype == "email" || $datatype == "host")
        {
            $this_section[2] .= '<input type="text" ';
            $this_section[2] .= 'name="'.$name.'" ';
            if (defined($name))
                $this_section[2] .= 'value="'.constant($name).'" ';
            elseif ($default)
                $this_section[2] .= 'value="'.$default.'" ';
            $this_section[2] .= '/>';
        }
        if ($required)
        {
            $this_section[2] .= '<span style="color:red">*</span>';
        }
        
        $this_section[2] .= "<br />";
    }
}
$sections[] = $this_section; // add the last section to the array

fclose($handle);

// actual validation and running of the pscripts needs to happen here before moving onto the next step
$errors = false;

if (sizeof($_POST) || $_GET['submit'])
{
include("pscripts/".$sections[$_SESSION['currentstep']][0].".php"); // run the pscript for this section
echo "Running pscripts/".$sections[$_SESSION['currentstep']][0].".php<br />";
}

if (!$errors)
    foreach ($_POST as $key => $value)
    {
        // add each post element to the accumulating array of variables to write to config
        $_SESSION['toconfig'][$key] = $value;
        $committedvalues = true;
    }
    
if (!$errors && ($committedvalues || $_GET['submit']))
{
    // if there are no validation errors, then proceed to the next step
    $passtonextstep = true;
}

print_r($_SESSION['toconfig']);
if ($passtonextstep)
    $_SESSION['currentstep']++;
$currentstep = $_SESSION['currentstep'];
$nextstep = $currentstep + 1;
$sectiontodisplay = ($currentstep)? $currentstep : 0;


?><br />
<a href="?reset=1">[reset]</a><?php

?><div style="color:red; font-weight:bold;"><?php
echo $errortext;
?></div><?php

?><form name="form" action="?submit=1" method="post"><?php
echo $sections[$sectiontodisplay][2];
?></form>
<?php

if ($nextstep < sizeof($sections))
    echo '<br /><span class="link" onclick="document.form.submit();">Next</span>';



?>
