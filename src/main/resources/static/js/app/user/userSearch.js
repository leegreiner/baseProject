var targetPageSetUp = function() {
  var responsiveHelper_dt_basic = undefined;
  var breakpointDefinition = {
      tablet : 1024,
      phone : 480
  };
  
  var dt = $('#searchResults').DataTable({
    'sDom': "<'dt-toolbar'<'col-xs-12 col-sm-6'f><'col-sm-6 col-xs-12 hidden-xs'l>r>t"+
      "<'dt-toolbar-footer'<'col-sm-6 col-xs-12 hidden-xs'i><'col-xs-12 col-sm-6'p>>",
    'autoWidth' : true,
    'serverSide': true,
      'ajax': {
        'url': '/api/users',
        'data': DataTables.flatten,
        'beforeSend': function (request) {
            ga('send', 'event', 'User', 'search');
          },
        'error': AjaxUtil.onLoadError
      },
    'columns': [
        { 'data': 'lastName' },
        { 'data': 'firstName' },
        { 'data': 'userName' },
        { 'data': 'email',
          'render': DataTables.renderEmail
        },
        { 'data': 'accountEnabled',
          'render': DataTables.renderYesNo
        }
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