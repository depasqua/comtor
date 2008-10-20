<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
  <link rel="icon" type="image/gif" href="favicon.gif" />
  <link rel="shortcut icon" type="image/vnd.microsoft.icon" href="favicon.ico" />
  <link rel="stylesheet" type="text/css" href="css/tables.css" />
  <link rel="stylesheet" type="text/css" href="css/layout.css" />
  <link rel="stylesheet" type="text/css" media="print" href="css/print.css" />

  <meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />

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

<!-- Modules -->
{if is_array($modules) && count($modules) > 0 }
<div class="sidelinks">
  <div class="mod_top"></div>
  <div class="side_mid">
    <div class="side_mid_content">
      <ul>
      {foreach from=$modules item="m"}
        <li><a href="{$m->href}" {foreach from=$m->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$m->name}</a></li>
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
        <ul>
           {foreach from=$comtorLinks item="c"}
            <li><a href="{$c->href}" {foreach from=$m->attrs item="a"}{$a->attr}="{$a->value}"{/foreach}>{$c->name}</a></li>
          {/foreach}
          <li><a href="features.php">Features We Measure</a></li>
          <li><a href="faq.php">FAQ</a></li>
        </ul>
      </div>
    </div>
    <div class="side_bottom"></div>
  </div>

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
  Copyright &copy; 2006 TCNJ
  <div class="tcnj_logo">
    <a href="http://www.tcnj.edu"><img src="img/tcnj_logo-small.gif" alt="TCNJ Logo" border="0" /></a>
  </div>
</div>

</body>
</html>
