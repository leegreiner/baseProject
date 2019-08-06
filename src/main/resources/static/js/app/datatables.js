var DataTables = {
    flatten: function(params) {
      'use strict';
      params.columns.forEach(function (column, index) {
        params['columns[' + index + '].data'] = column.data;
        params['columns[' + index + '].name'] = column.name;
        params['columns[' + index + '].searchable'] = column.searchable;
        params['columns[' + index + '].orderable'] = column.orderable;
        params['columns[' + index + '].search.regex'] = column.search.regex;
        params['columns[' + index + '].search.value'] = column.search.value;
      });
      
      delete params.columns;

      params.order.forEach(function (order, index) {
        params['order[' + index + '].column'] = order.column;
        params['order[' + index + '].dir'] = order.dir;
      });
      
      delete params.order;

      params['search.regex'] = params.search.regex;
      params['search.value'] = params.search.value;
      delete params.search;

      return params;
    },
    
    renderEmail: function(data,type,full,meta){
    	'use strict';
    	return '<a href="mailto:' + data + '" target="_top">' + data +'</a>';
    },
    
    renderYesNo: function(data,type,full,meta){
      'use strict';
      return (data === true) ? 'Yes' : 'No';
    },
    
    renderLink: function(url, newWindow, resourceName, displayProperty, data, type, full, meta) {
      'use strict';
      var displayValue = '';
      
      try {
        displayValue = full[displayProperty];
      } catch (e) {}
      
      if (displayValue === null) {
        displayValue = '';
      }
      
      if (displayValue !== '') {
        var resource = full[resourceName];
        var target = (newWindow === true) ? ' target="_blank"' : '';
        return '<a href="' + url + resource + '"' + target + '>' + displayValue + '</a>';
      } else {
        return '';
      }
    },
}