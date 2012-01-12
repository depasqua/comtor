<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<link rel="stylesheet" type="text/css" href="styles/login.css" media="screen" />
		<title>/** COMTOR **/</title>
	</head>

	<body>
		<h1 class="centered" style="color: white; position: absolute; left: 30%;">/** COMTOR **/</h1>
		<div class="centered"> 
			<form action="FileAndJar" class="centered" enctype="multipart/form-data" method="POST">
				<label>E-mail:</label><br/>
				<input type="text" name="email"><br/>
				<label>File:</label><br/>
				<input type="file" name="jarfile"><br/>
				<input type="Submit" class="submit" value="Upload File"><br/>
			</form>
		</div>
	</body>
</html>