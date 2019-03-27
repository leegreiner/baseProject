var $loginForm = $("#loginForm").validate({
	errorClass		: 'invalid',
	errorElement	: 'em',
	highlight: function(element) {
	    $(element).parent().removeClass('state-success').addClass("state-error");
	    $(element).removeClass('valid');
	},
	unhighlight: function(element) {
	    $(element).parent().removeClass("state-error").addClass('state-success');
	    $(element).addClass('valid');
	},
	
	rules : {
		username : {
			required: true
		},
		password : {
			required: true
		}
	},
	
	messages : {
		username: {
			required: 'Please enter your user name'
		},
		password: {
			required: 'Please enter your password'
		}
	},

	errorPlacement: function(error, element) {
		error.insertAfter(element.parent());
	}
});