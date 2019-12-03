var AjaxUtil = {
  ajaxError: function(xhr, textStatus, error) {
    console.log(error + ", " + xhr.status);
    console.log(xhr.getAllResponseHeaders());
    
    if (xhr.status === 302 || xhr.status === 401) {
      AjaxUtil.directToIndex();
    } else {
      var json = JSON.parse(xhr.responseText);
      
      bootbox.alert( {
        title: "<span style='color: red'>An error has occurred</span>",
        message: json.error.message
      });
    }
  },
  directToIndex: function() {
    var index = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
    window.location.replace(index + '/?sessionExpired=true');
  }
};