$(document).ready(function() {
	pageSetUp();
	
	/* if pages need to perform their own initialization 
	 * define a function named 'targetPageSetup'.
	 */
	if (typeof targetPageSetUp === 'function') {
		targetPageSetUp();
	}
})