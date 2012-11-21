<!DOCTYPE html>
<html lang="en">
	<head>
		<title>COMTOR - Request API Key</title>

		<link rel="stylesheet" href="stylesheets/app.css">
		<link rel="stylesheet" href="stylesheets/foundation.css">
		<link rel="stylesheet" href="stylesheets/ie.css">

		<script type="text/javascript">
		  var _gaq = _gaq || [];
		  _gaq.push(['_setAccount', 'UA-1641868-7']);
		  _gaq.push(['_trackPageview']);
		
		  (function() {
			var ga = document.createElement('script'); ga.type = 'text/javascript'; ga.async = true;
			ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www') + '.google-analytics.com/ga.js';
			var s = document.getElementsByTagName('script')[0]; s.parentNode.insertBefore(ga, s);
		  })();
		</script>

		<script type="text/javascript" src="scripts/jquery.min.js"></script>
		<script type="text/javascript" src="scripts/app.js"></script>
		<script type="text/javascript" src="scripts/foundation.js"></script>
		<script type="text/javascript" src="scripts/modernizr.foundation.js"></script>
	</head>

	<body>
	<div class="container">
		<div class="row" style="margin-top: 30px; margin-bottom:15px;">
			<div class="six columns centered">
				<div style="text-align: center">
					<img src="images/comtor/comtorLogo.png" alt="COMTOR logo"/>
					<p style="font-size: small; font-weight: bold">Need a COMTOR API Key? Get one here!</p>
				</div>
			</div>
		</div>

		<div class="row">
			<div class="eight columns centered">
				<h3>Key Exists!</h3>
				<p>We're sorry, but it seems that this email has previously requested an API for COMTOR. As a courtesy, we have sent a reminder email to <% out.print(request.getParameter("email")); %>.</p>

				<p>If you did not request this API key, or would like to render the key inactive, please contact the COMTOR project administrators at <a href="mailto:comtor@tcnj.edu">comtor@tcnj.edu</a></p>
			</div>
		</div>

		<div class="row">
			<div class="six columns centered">
				<div style="text-align: center;">
					Learn more about the <a href="http://www.comtor.org">COMTOR project</a> today.<br/>
					See our <a href="faq.html">FAQ</a>!
				</div>
			</div>
		</div>
	</div>
	</body>
</html>

