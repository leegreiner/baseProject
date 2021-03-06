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
        ga('send', 'event', 'User', 'updateProfile');
      }
      
      form.classList.add('was-validated');
    }, false);
  });
}