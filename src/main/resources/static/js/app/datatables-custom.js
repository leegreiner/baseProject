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
    }
}