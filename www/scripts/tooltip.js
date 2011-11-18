$(document).ready(function() {
	toolTipSetup(); // Calls the toolTip setup when the page loads.
	
	// Ensures that the tooltip is re-setup every time the window is resized.
	window.onresize = function(event) {
		toolTipSetup();
	}	
	
	$(".help_button").click(function (){
		toolTipOpen();
	});
	
   	$(".help_close_button").click(function () {
		toolTipClose();
	});
});


// Sets the height of the box to half the window height, and button
// and box to the middle of the screen.
function toolTipSetup() {
	var height;
	
	
	height = $(window).height() / 2;
	
	$('.help_popup').css('height', height);
	$('.viewport').css('height', (height-40));
	
	$('.help_button').css('top', (height-12));
	$('.help_popup').css('top', (height/2));
}

// Opens the tooltip.
function toolTipOpen() {
	$(".help_popup").show("slide", { direction: "right" }, 1000);
	$('#scrollbar1').tinyscrollbar();
}

// Closes the tooltip
function toolTipClose() {
	$(".help_popup").hide("slide", { direction: "right" }, 1000);
}
