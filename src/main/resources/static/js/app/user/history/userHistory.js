var targetPageSetUp = function() {
  'use strict';
  
  $('#history').dataTable({
    paging: false,
    searching: false,
    fixedHeader: true,
    scrollX: true,
    scrollCollapse: true
  });
}