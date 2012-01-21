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
	<script type="text/javascript" src="jQuery/js/jquery.tinyscrollbar.min.js"></script>
	<script type="text/javascript" src="jQuery/mousewheel/jquery.mousewheel.js"></script>
	<script type="text/javascript" src="scripts/tooltip.js"></script>

	<title>{$windowTitle|default:"COMTOR"}</title>
</head>

<body>

<div id="header_bar">
	<img id="logo" alt="COMTOR Logo" src="img/logo.gif" />
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
				var url = "?courseId=" + courseId;
				window.location = url;
			}
		}
		//-->
		</script>
		{/literal}
		<select id="courseId" name="courseId" onchange="CourseSelected();">
		<option value="-1">Course Select</option>
			{foreach from=$courses item="c"}
			<option value="{$c.courseId}" {if $smarty.session.courseId == $c.courseId}selected="selected"{/if}>{$c.semester}: {$c.section}</option>
			{/foreach}
		</select>
	</div>
	{/if}
  
	{if isset($smarty.session.username)}
		<div class="username">{$smarty.session.username} <a href="logout.php">(Logout)</a></div>
	{/if}
  
	{if $smarty.const.DEVELOPMENT|default:false}
		<div style="float:left; font-size: 20px; font-weight: bold; color: #a00000;">Development Version</div>
	{/if}
</div>

<!-- Sidebar -->
<div class="sidebar">

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
				<li><a class="moduleLink" href="{$m->href}" {foreach from=$m->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$m->name}</a></li>
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
	<div class="side_mid">
		<div class="side_mid_content">
			<ul id="sidebar_2">
				{foreach from=$comtorLinks item="c"}
				{if $c->heading|default:false}
				<li class="heading" {foreach from=$c->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$c->name}</li>
				{else}  
				<li class="button"><a href="{$c->href}" {foreach from=$m->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$c->name}</a></li>
				{/if}
				{/foreach}
				<li class="heading">General</li>
				<li class="button"><a href="bugsAndFeatures.php">Bugs / Features Trackers</a></li>
				<li class="button"><a href="features.php">Features We Measure</a></li>
				<li class="button"><a href="faq.php">FAQ</a></li>
				<li class="button"><a href="tutorials.php">Video Tutorials</a></li>
				<li class="button"><a href="privacy.php">Privacy Policy</a></li>
			</ul>
		</div>
	</div>
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
	if (document.cookie.length > 0) {
		var cookies = document.cookie.split(';');
		for (i = 0; i < cookies.length; i++) {
			var idx = cookies[i].indexOf(name+"="); 
			if (idx == 0)
				return cookies[i].substring(name.length+1);
		} 
	}
	return "";
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
	
		<!-- Tooltips -->
		{if isset($tooltips) }
		<div class="help_button help_slider"></div>
		<div class="help_popup help_slider">
		<div class="help_close_button"><a id="close">CLOSE<img src="img/icons/closeX.png"/></a></div>
		<div id="scrollbar1">
		<div class="scrollbar">
		<div class="track">
		<div class="thumb">
		<div class="end">
		</div>
		</div>
		</div>
		</div>
		<div class="viewport">
			<div class="overview">
				{$tooltips}	
			</div>
		</div>
		</div>
		</div>
		{/if}
		<!-- End Tooltips -->
		{$tpldata}
		
		</div>
	</div>
</div>

{php}
	$this->assign('currentYear', date("Y"));
{/php}

<div class="footer">
	<a href="about.php">About Comment Mentor</a><br/>
	<small>Version 2.0.2 - 1/21/12 - Copyright &copy; {$currentYear} TCNJ</small>
	<div class="tcnj_logo">
		<a href="http://www.tcnj.edu"><img src="img/tcnj_logo-small.gif" alt="TCNJ Logo" border="0" /></a>
	</div>
</div>

</body>
<!-- Google Analytics Code -->
{literal}
<script type="text/javascript">

  var _gaq = _gaq || [];
  _gaq.push(['_setAccount', 'UA-1641868-6']);
  _gaq.push(['_trackPageview']);

  (function() {
    var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
    ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
    var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
  })();

</script>
{/literal}
</html>
