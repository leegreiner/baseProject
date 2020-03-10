var buttonUtils = (function() {
  var disableButton = function(button) {
    $('#' + button).prop('disabled', true);
  }
  
  var enableButton = function(button) {
    $('#' + button).prop('disabled', false);
  }
  
  var disableLink = function(link) {
    $('#' + link).addClass('disabled');
  }
  
  var enableLink = function(link) {
    $('#' + link).removeClass('disabled');
  }
  
  return {
    disableButton: disableButton,
    enableButton: enableButton,
    disableLink: disableLink,
    enableLink: enableLink
  }
})();