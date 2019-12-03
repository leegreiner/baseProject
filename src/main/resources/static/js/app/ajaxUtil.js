var AjaxUtil = {
  ajaxError: function(xhr, textStatus, error) {
    console.log(error + ", " + xhr.status);
    console.log(xhr.responseText);
    
    if (xhr.status === 302 || xhr.status === 401) {
      AjaxUtil.directToIndex();
    } else {
      var message = "An unknown error has occurred (" + xhr.status + ")";
      
      if (AjaxUtil.isJson(xhr.responseText)) {
        message = JSON.parse(xhr.responseText).message;
      }
      
      bootbox.alert( {
        title: "<span style='color: red'><h3>An error has occurred!</h3></span>",
        message: message,
        backdrop: true,
        closeButton: false
      });
    }
  },
  directToIndex: function() {
    var index = location.protocol+'//'+location.hostname+(location.port ? ':'+location.port: '');
    window.location.replace(index + '/?sessionExpired=true');
  },
  isJson: function(str) {
    try {
      JSON.parse(str);
    } catch (e) {
      return false;
    }
    return true;
  }
};