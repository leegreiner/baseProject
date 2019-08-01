var targetPageSetUp = function() {
  'use strict';
  
  $('.select2').select2();

  var forms = document.getElementsByClassName('needs-validation');

  var validation = Array.prototype.filter.call(forms, function(form) {
    form.addEventListener('submit', function(event) {
      if (form.checkValidity() === false) {
          event.preventDefault();
          event.stopPropagation();
      }
      
      form.classList.add('was-validated');
    }, false);
  });
/*  
  var validator = $("#userProfileFile").validate({
    ignore: [],
    rules: {
      timeZone: {
        required: true
      },
      name: {
        required: true
      }
    },
    errorElement: 'span',
    errorPlacement: function (error, element) {
        error.addClass('invalid-feedback');
        element.closest('.form-group').append(error);
    },
    highlight: function (element, errorClass, validClass) {
      $(element).addClass('is-invalid').removeClass('is-valid');
    },
    unhighlight: function (element, errorClass, validClass) {
      $(element).removeClass('is-valid').removeClas('is-invalid');
    }
  });
*/
}