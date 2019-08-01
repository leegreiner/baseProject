var targetPageSetUp = function() {
  var dt = $('#searchResults').dataTable({
    responsive: true,
    fixedHeader: true,
    serverSide: true,
    ajax: {
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
    ]
  });
}