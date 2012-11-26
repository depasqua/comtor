<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@ taglib uri='http://java.sun.com/jsp/jstl/core' prefix='c'%>
<!DOCTYPE html>

<!-- paulirish.com/2008/conditional-stylesheets-vs-css-hacks-answer-neither/ -->
<!--[if lt IE 7]> <html class="no-js lt-ie9 lt-ie8 lt-ie7" lang="en"> <![endif]-->
<!--[if IE 7]>    <html class="no-js lt-ie9 lt-ie8" lang="en"> <![endif]-->
<!--[if IE 8]>    <html class="no-js lt-ie9" lang="en"> <![endif]-->
<!--[if gt IE 8]><!--> <html class="no-js" lang="en"> <!--<![endif]-->
<head>
	<meta charset="utf-8" />
	<!-- Set the viewport width to device width for mobile -->
	<meta name="viewport" content="width=device-width" />

	<title>COMTOR Admin</title>

	<!-- Included CSS Files -->
	<link rel="stylesheet" href="./stylesheets/app.css">

	<!--[if lt IE 9]>
		<link rel="stylesheet" href="./stylesheets/ie.css">
	<![endif]-->

	<script src="./javascripts/foundation/modernizr.foundation.js"></script>

	<!-- IE Fix for HTML5 Tags -->
	<!--[if lt IE 9]>
		<script src="http://html5shiv.googlecode.com/svn/trunk/html5.js"></script>
	<![endif]-->
	
	<!--  jQuery -->
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	
	<link rel="stylesheet" href="./stylesheets/foundation.css" type="text/css" media="all"/>  

	<style>
		body{
			background-image: URL('http://subtlepatterns.com/patterns/brillant.png');
			background-repeat: repeat;
		}
		table{
			width: 100%;
		}
		tr:hover :not(th){
			cursor: pointer;
			color: white;
			background-color: #4099FF;
		}
		tr:hover td{
			cursor: pointer;
			color: white;
			background-color: #4099FF;
		}
		#populate:hover{
			text-decoration: underline;
			cursor: pointer;
		}
	</style>
</head>
<body>
	<!-- container -->
	<div class="container">

		<div class="row">
			<div class="twelve columns">
				<h2>COMTOR Admin <small>Keep an eye on COMTOR.</small></h2>
			</div>
		</div>
		
		<!--  Begin Navigation -->
		<div class="row">
		<div class="twelve columns">
			<ul class="nav-bar">
			  <li class="active"><a href="#">API Keys</a></li>
			  <li><a href="#">Server Health</a></li>
			</ul><!--  End Navigation -->
		</div>
		</div>
		
		<div class="row">
			<div class="twelve columns">
				<!--  Contains success and alert messages -->
				<div id="messages"></div>
				
				<!--  Tool bar -->
				<div id="bulk" class="button-bar">
				  <ul class="button-group">
				    <li><button id="delete" class="alert button">Delete</button></li>
				    <li><button disabled="disabled" class="button">Deactivate</button></li>
				  </ul>
				
				  <ul id="single" class="button-group">
				    <li><button disabled="disabled" class="button">View</button></li>
				    <li><button disabled="disabled" class="button">Edit</button></li>
				  </ul>
				</div><!--  End Tool Bar -->
			</div>
		</div>
		<div class="row">
			<div class="twelve columns">
				<table id="keys">
					<thead>
						<th><input type="checkbox" id="checkAll" /></th>
						<th>Email</th>
						<th>API Key</th>
						<th>Host</th>
						<th>Date</th>
						<th>IP</th>
					</thead>
					<c:forEach var="result" items="${result}">
						<tr>
							<td class="checkbox"><input type="checkbox"/></td>
							<td id="email"><c:out value="${result['email'].getS()}"/></td>
							<td id="key"><c:out value="${result['apikey'].getS()}"/></td>
							<td><c:out value="${result['host'].getS()}"/></td>
							<td><c:out value="${result['date'].getS()}"/></td>
							<td><c:out value="${result['ip'].getS()}"/></td>
						</tr>
					</c:forEach>
				</table>
				
				<!--  Begin Pagination -->
				<ul class="pagination">
				  <li class="arrow unavailable"><a href="">&laquo;</a></li>
				  <li class="current"><a href="#">1</a></li>
				  <li class="arrow"><a href="#">&raquo;</a></li>
				</ul><!--  End Pagination -->
				
			</div>
			<hr />
			<footer>
				<p>2012 - COMTOR</p>
			</footer>
		</div>
	</div>
		<script>
			// Populate Table
			function populateTable(){
				$.ajax({
					  url: "http://localhost:8080/CloudAdmin/console",
					}).done(function ( data ) {
						console.log(data)
					});
			}
			$('#populate').click(function(){
				populateTable();
			});
			// Function to un/highlight rows depending on checkbox state
			$(":checkbox").click(function(){
				$cell = $(this).parent();
				if($(this).is(':checked')){
					$cell.parent().css('background-color', '#4099FF');	
				}else{
					$cell.parent().css('background-color', '#F7F7F7');
				}
				$('#keys tr').each(function(){
					if($('td.checkbox :input', this).is(':checked')){
					}
				});
			});
			// NOT WORKING YET - Check all checkboxes
			function checkAll(){
				$('table tr').each(function(){
					var checkbox = $(this).find('input:checkbox');
					checkbox.attr('checked', true);
				});
			}
			// Send Action call to Servlet for processing
			function callAction(action, key, email){
				switch(action){
				case "delete":
					$.ajax({
						  url: "http://localhost:8080/CloudAdmin/console?key="+key+"&email="+email+"&do="+action,
						}).done(function ( data ) {
							$('#messages').append('<div class="alert-box success">Item successfully deleted. Refreshing in 3 seconds. <a href="" class="close">&times;</a></div>');
	
							// Reload page after 3 seconds
							setTimeout(function(){
								window.location.reload(1);
							}, 3000);
						});
				}
			}
			function makeSure(action, number){
				return confirm("Are you sure you want to "+action+" "+number+" items?");
			}
			// ACTION = DELETE: Get keys and emails to send to servlet for processing
			$("#delete").click(function() {
				var key = "";
				var email = "";
				var numAffected = 0;
				
				// Need to create arrays for POST of all the data for all keys to delete
				  $('#keys tr').filter(':has(:checkbox:checked)').each(function() {
				        $tr = $(this);
				        numAffected++;
				        $('td#email', $tr).each(function() {
				        	email = $(this).text();
				        });
				        $('td#key', $tr).each(function() {
				        	key = $(this).text();
				        });
				    });
				if(numAffected == 0){
					alert("No items selected.");
				}else if(makeSure("delete", numAffected)){
		        	callAction("delete", key, email);
		        }
			});
		</script>
		<!--  Foundation 3 JS Files -->
		<script src="./javascripts/app.js" ></script>
		<script src="./javascripts/jquery.tooltips.js" ></script>
		<script src="./javascripts/jquery.placeholder.min.js" ></script>
		<script src="./javascripts/jquery.reveal.js" ></script>
		<!--  End Foundation 3 Files -->
</body>
</html>