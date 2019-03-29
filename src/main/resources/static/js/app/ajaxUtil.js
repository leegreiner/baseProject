var AjaxUtil = {
  ajaxError: function(jqXHR, textStatus, errorThrown, text) {
    console.log(textStatus + ", " + jqXHR.status);
    console.log(jqXHR.getAllResponseHeaders());
    
    if (jqXHR.status === 0 || jqXHR.status === 302 || jqXHR.status === 401) {
      AjaxUtil.directToIndex();
    } else {
      $.alert({
        icon: 'glyphicon glyphicon-exclamation-sign',
        title: 'Error',
        content: text,
        type: 'red',
        columnClass: 'medium'
      });
    }
  },
  directToIndex: function() {
    var index = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
    window.location.replace(index + '/?sessionExpired=true');
  }
};