var targetPageSetUp = function() {
'use strict';
  $.fn.dataTable.moment(BP_GLOBALS.getDateTimeFormat());
  
  $('#history').dataTable({
    paging:   false,
    searching: false,
    fixedHeader: true,
    scrollX: true,
    scrollY: "500px",
    scrollCollapse: true
  });
}