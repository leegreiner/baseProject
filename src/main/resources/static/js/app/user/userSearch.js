var targetPageSetUp = function() {
  var dt = $('#searchResults').dataTable({
    responsive: true,
    fixedHeader: true,
    serverSide: true,
    processing: true,
    language: {
      processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i>'
    },
    ajax: {
      'url': '/api/users',
      'data': DataTables.flatten,
      'beforeSend': function (request) {
          ga('send', 'event', 'User', 'search');
        },
      'error': AjaxUtil.ajaxError
    },
    'columns': [
      { 'data': 'lastName' },
      { 'data': 'firstName' },
      { 'data': 'username',
        'render': function(data, type, full, meta) {
          if ($('#searchResults').data('edit')) { 
            return DataTables.renderLink('/users/', false, 'id', 'username', data, type, full, meta);
          } else {
            return full['username'];
          }
        }
      },
      { 'data': 'email',
        'render': DataTables.renderEmail
      },
      { 'data': 'accountEnabled',
        'render': DataTables.renderYesNo
      }
    ]
  });
}