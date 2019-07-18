var DataTables = {
    customize: function(dataTable, searchInputId) {
      var sizeId = searchInputId + 'Select';
      $("div.dataTables_filter").html('<label><span class="input-group-addon"><i class="glyphicon glyphicon-search"></i></span><input id="' + searchInputId + '" type="search" class="form-control" placeholder aria-controler="searchResults"></input></label>');
      $("div.dataTables_length").html('<select id="' + sizeId + '" name="searchResults_length" aria-controls="searchResults" class="form-control input-sm"><option value="10">10</option><option value="25">25</option><option value="50">50</option><option value="100">100</option></select>');
      
      $('#' + searchInputId).on( 'keyup', function () {
        dataTable.search($('#' + searchInputId).val()).draw();
      });
      
      $('#' + sizeId).on( 'change', function () {
        dataTable.page.len($('#' + sizeId + ' option:selected').text()).draw();
      });
    },

    flatten: function(params) {
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
    	"use strict";
    	return '<a href="mailto:' + data + '" target="_top">' + data +'</a>';
    },
    
    renderYesNo: function(data,type,full,meta){
    	"use strict";
        return (data === true) ? 'Yes' : 'No';
    }
}