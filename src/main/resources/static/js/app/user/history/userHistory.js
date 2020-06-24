var targetPageSetUp = function() {
  'use strict';
  
  $('#history').dataTable({
    scrollResize: true,
    scrollY: '45vh',
    paging: false,
    searching: false,
    fixedHeader: true,
    scrollX: true,
    scrollCollapse: true
  });
}