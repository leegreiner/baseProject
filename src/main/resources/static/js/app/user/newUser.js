var targetPageSetUp = function() {
  'use strict';
  
  $('.select2').select2();

  var forms = document.getElementsByClassName('needs-validation');

  var validation = Array.prototype.filter.call(forms, function(form) {
    form.addEventListener('submit', function(event) {
      if (form.checkValidity() === false) {
          event.preventDefault();
          event.stopPropagation();
      } else {
        buttonUtils.disableButton('submitButton');
        buttonUtils.disableLink('cancelLink');
        
        ga('send', 'event', 'User', 'new');
      }
      
      form.classList.add('was-validated');
    }, false);
  });
}