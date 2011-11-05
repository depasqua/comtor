<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<!-- The HTML 4.01 Transitional DOCTYPE declaration-->
<!-- above set at the top of the file will set     -->
<!-- the browser's rendering engine into           -->
<!-- "Quirks Mode". Replacing this declaration     -->
<!-- with a "Standards Mode" doctype is supported, -->
<!-- but may lead to some differences in layout.   -->

<html>
  <head>
<style type="text/css">
table, td
{
    border-color: #000000;
    border-style: solid;
}

table
{
    border-width: 0 0 1px 1px;
    border-spacing: 0;
    border-collapse: collapse;
}

td
{
    margin: 0;
    padding: 4px;
    border-width: 1px 1px 0 0;
    background-color: #F0F0F0;
}
</style>

<meta http-equiv="content-type" content="text/html; charset=UTF-8">
<title>/** COMTOR **/</title>
  </head>

  <body>
	
    <table border="1" cellspacing="1" cellpadding="4">
    <tr>
    <td colspan="4" style="font-weight:bold; text-align: center;">
    <h1>/** COMTOR **/ Servlets</h1>
    </td>
    </tr>
      <tr>
        <td colspan="2" style="font-weight:bold;">List of Servlets</td> 
        <td colspan="2" style="font-weight:bold;">Function</td>        
      </tr>
      <tr>
        <td colspan="2" ><a href="jarupload.jsp">Upload and Extract JAR</a></td>
        <td colspan="2" >Asks the user to Upload a File then extracts the Jar in the files directory. It then sends an email to the address the user inputs.</td>
      </tr>
    </table>
  </body>
</html>
