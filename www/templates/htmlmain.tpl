<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<link rel="icon" type="image/gif" href="favicon.gif" />
	<link rel="shortcut icon" type="image/vnd.microsoft.icon" href="favicon.ico" />
	<link rel="stylesheet" type="text/css" href="css/tables.css" />
	<link rel="stylesheet" type="text/css" href="css/layout.css" />
	<link rel="stylesheet" type="text/css" media="print" href="css/print.css" />
	<link rel="stylesheet" type="text/css" href="jQuery/css/custom-theme/jquery-ui-1.8.custom.css"  />
	<style type="text/css">@import "jQuery/timeentry/jquery.timeentry.css";</style> 

	<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

	<script type="text/javascript" src="jQuery/js/jquery-1.4.2.min.js"></script>
	<script type="text/javascript" src="jQuery/js/jquery-ui-1.8.custom.min.js"></script>	
	<script type="text/javascript" src="jQuery/timeentry/jquery.timeentry.js"></script>

	<title>{$windowTitle|default:"COMTOR"}</title>
</head>

<body>
{if $smarty.const.DEVELOPMENT|default:false}
  <div style="position: absolute; right: 10px; font-size: 50px; font-weight: bold; color: #a00000;">Development</div>
{/if}

<img alt="COMTOR Logo" src="img/logo.gif" />

<div class="header_bar">
  <div class="bar_left"></div>
  <div class="bar_right"></div>
  <div class="bar_mid_frame">
    <div class="bar_mid">
      {if isset($smarty.session.username)}
        <div class="username">{$smarty.session.username}<a href="logout.php">(Logout)</a></div>
      {/if}
      {if isset($courses) && count($courses) > 0}
        <div class="course_select">
          {if $smarty.session.acctType == 'student'}
            {assign var='coursePage' value='reports.php'}
          {else}
            {assign var='coursePage' value='courseManage.php'}
          {/if}
          {literal}
          <script type="text/javascript">
          <!--
          function CourseSelected()
          {
            var courseId = document.getElementById("courseId").value;
            if (courseId != -1 {/literal}{if isset($smarty.session.courseId)}&& courseId != {$smarty.session.courseId}{/if}{literal})
            {
              //var url = "{/literal}{$coursePage}{literal}?courseId=" + courseId;
              var url = "?courseId=" + courseId;
              window.location = url;
            }
          }
          //-->
          </script>
          {/literal}
          <select id="courseId" name="courseId" onchange="CourseSelected();" >
            <option value="-1">Course Select</option>
            {foreach from=$courses item="c"}
              <option value="{$c.courseId}" {if $smarty.session.courseId == $c.courseId}selected="selected"{/if}>{$c.semester}: {$c.section}</option>
            {/foreach}
          </select>
        </div>
      {/if}
    </div>
  </div>
</div>

<!-- Sidebar -->
<div class="sidebar">

<div class="sidelinks" style="text-align: center;">
  <a href="javascript:void(0);" id="contrast_link" onclick="changeContrast(this);">High Contrast</a>
</div>

<!-- Modules -->
{if is_array($modules) && count($modules) > 0 }
<div class="sidelinks">
  <div class="mod_top"></div>
  <div class="side_mid">
    <div class="side_mid_content">
      <ul id="sidebar_1">
      {foreach from=$modules item="m"}
        {if $m->heading|default:false}
          <li class="heading" {foreach from=$m->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$m->name}</li>
        {else}
          <li><a href="{$m->href}" {foreach from=$m->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$m->name}</a></li>
        {/if}
      {/foreach}
      </ul>
    </div>
  </div>
  <div class="side_bottom"></div>
</div>
{/if}

  <!-- Comtor Links -->
  <div class="sidelinks">
    <div class="comtor_top"></div>
    <div class="side_mid">
      <div class="side_mid_content">
        <ul id="sidebar_2">
           {foreach from=$comtorLinks item="c"}
             {if $c->heading|default:false}
              <li class="heading" {foreach from=$c->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$c->name}</li>
            {else}  
              <li><a href="{$c->href}" {foreach from=$m->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$c->name}</a></li>
            {/if}
          {/foreach}
          <li class="heading">General</li>
          <li><a href="features.php">Features We Measure</a></li>
          <li><a href="faq.php">FAQ</a></li>
          <li><a href="tutorials.php">Video Tutorials</a></li>
          <li><a href="privacy.php">Privacy Policy</a></li>
        </ul>
      </div>
    </div>
    <div class="side_bottom"></div>
  </div>


{literal}
<script type="text/javascript">
<!--

function setCookie(name, value, validDays)
{
  var expire_date = new Date();
  expire_date.setDate(expire_date.getDate() + validDays);
  document.cookie = name + "=" + escape(value) + ((validDays == null) ? "" : "; expires="+expire_date.toGMTString());
}

function getCookie(name)
{
  if (document.cookie.length > 0)
  {
    var cookies = document.cookie.split(';');
    for (i = 0; i < cookies.length; i++)
    {
      var idx = cookies[i].indexOf(name+"="); 
      if (idx == 0)
        return cookies[i].substring(name.length+1);
    } 
  }
  return "";
}

var link = document.getElementById("contrast_link");
if (link != null)
{
  var contrast = getCookie("contrast");
  if (contrast == "high")
    changeContrast(link);
}

function changeContrast(elem)
{
  var s1 = document.getElementById("sidebar_1");
  var s2 = document.getElementById("sidebar_2");
    
  if (elem.innerHTML == "High Contrast")
  {
    if (s1 != null)
      s1.className = "contrast";
    
    if (s2 != null)
      s2.className = "contrast";
    
    elem.innerHTML = "Normal Contrast";    
    setCookie("contrast", "high", 1);
  }
  else
  {
    if (s1 != null)
      s1.className = "";
    
    if (s2 != null)
      s2.className = "";
      
    elem.innerHTML = "High Contrast";
    setCookie("contrast", "normal", 1);
  }
}

//-->
</script>
{/literal}


<!-- End Sidebar -->
</div>

<div class="main_column">
  <div class="content_frame">
    <div class="content">

    {* Breadcrumbs *}
    {include file="template_breadcrumbs.tpl"}

    {if isset($success) }<div class="success">{$success}</div>{/if}
    {if isset($error) }<div class="error">{$error}</div>{/if}

    {$tpldata}

    </div>
  </div>
</div>

<div class="footer">
  <a href="about.php">About Comment Mentor</a><br/>
  Copyright &copy; 2009 TCNJ
  <div class="tcnj_logo">
    <a href="http://www.tcnj.edu"><img src="img/tcnj_logo-small.gif" alt="TCNJ Logo" border="0" /></a>
  </div>
</div>

<!-- Google Analytics Code -->
{literal}
<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-1641868-4");
pageTracker._trackPageview();
} catch(err) {}</script>
{/literal}

</body>
</html>
