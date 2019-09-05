var targetPageSetUp = function() {
  'use strict';

  var forms = document.getElementsByClassName('needs-validation');

  var validation = Array.prototype.filter.call(forms, function(form) {
    form.addEventListener('submit', function(event) {
      if (form.checkValidity() === false) {
          event.preventDefault();
          event.stopPropagation();
      } else {
        ga('send', 'event', 'User', 'password reset');
      }
      
      form.classList.add('was-validated');
    }, false);
  });
}