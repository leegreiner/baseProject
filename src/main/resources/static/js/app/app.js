var BP_GLOBALS = (function() {
  var my = {}
  
  my.getDateTimeFormat = function(id) {
    return 'YYYY-MM-DD HH:mm';
  }
  
  return my;
}());

$(document).ready(function() {
  if ($.fn.dataTable && $.fn.dataTable.moment) {
    $.fn.dataTable.moment(BP_GLOBALS.getDateTimeFormat());
  }
  
  /* if pages need to perform their own initialization 
   * define a function named 'targetPageSetup'.
   */
  if (typeof targetPageSetUp === 'function') {
  	targetPageSetUp();
  }
})