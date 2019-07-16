var targetPageSetUp = function() {
  var responsiveHelper_dt_basic = undefined;
  var breakpointDefinition = {
      tablet : 1024,
      phone : 480
  };
  var token = $("meta[name='_csrf']").attr("content"); 
  var header = $("meta[name='_csrf_header']").attr("content");
  
  var dt = $('#searchResults').DataTable({
    'sDom': "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>t"+
      "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
    'autoWidth' : true,
    'serverSide': true,
      'ajax': {
        'url': '/api/users',
        'contentType': 'application/json',
        'type': 'POST',
        'beforeSend': function (request) {
          request.setRequestHeader(header, token);
        },
        'data': function (d) {
          return JSON.stringify(d);
        },
        'error': AjaxUtil.onLoadError
      },
    'columns': [
        { 'data': 'lastName' },
        { 'data': 'firstName' }
    ],
    'preDrawCallback' : function() {
      // Initialize the responsive datatables helper once.
      if (!responsiveHelper_dt_basic) {
        responsiveHelper_dt_basic = new ResponsiveDatatablesHelper($('#searchResults'), breakpointDefinition);
      }
    },
    'rowCallback' : function(nRow) {
      responsiveHelper_dt_basic.createExpandIcon(nRow);
    },
    'drawCallback' : function(oSettings) {
      responsiveHelper_dt_basic.respond();
    }  
  });
  
  DataTables.customize(dt, 'searchControl');
}