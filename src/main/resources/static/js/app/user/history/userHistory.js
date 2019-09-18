var targetPageSetUp = function() {
'use strict';
  $.fn.dataTable.moment('YYYY-MM-DD HH:mm');
  
  $('#history').dataTable({
    paging:   false,
    searching: false,
    fixedHeader: true,
    scrollX: true,
    scrollY: "500px",
    scrollCollapse: true
  });
}