<?php
$handle = fopen("../migrations/1.2/config/configspec.txt", "r");
while (($data = fgetcsv($handle, 1000, ",")) !== FALSE) {
    $num = count($data);
    if (substr($data[0], 0, 1) == "[") // if the line starts with "[", then it's a new section
    {
        // echo "section <b>".substr($data[0], 1, strlen($data[0])-2)."</b><br />";
        $titlenext = true;
    }
    elseif ($titlenext)
    {
        echo "<h1>".$data[0]."</h1>\n";
        $titlenext = false;
        $descnext = true;
    }
    elseif ($descnext)
    {
        echo "Description: ".$data[0]."\n";
        $descnext = false;
    }
    else
    {
        //echo "<p> $num fields in line $row: <br /></p>\n";
        $name = $data[0];
        $datatype = $data[1];
        $default = $data[2];
        $prompt = $data[3];
        $required = ($data[4] == 'r')? true : false;
        
        echo "\n".$prompt;
        
        if ($datatype == "string" || $datatype == "email" || $datatype == "host")
        {
            echo '<input type="text" ';
            echo 'name="'.$name.'" ';
            if ($default)
                echo 'value="'.$default.'" ';
            echo '/>';
        }
        
        echo "<br />";
    }
}
fclose($handle);
?>
