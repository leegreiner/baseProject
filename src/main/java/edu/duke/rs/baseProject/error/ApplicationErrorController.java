package edu.duke.rs.baseProject.error;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ApplicationErrorController implements ErrorController {
	public static final String ERROR_PATH = "/error";
	public static final String BASE_ERROR_VIEW = "error";

	@GetMapping("/error")
	public String handleError(final HttpServletRequest request) {
		final Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String result = "/unknownError";
		
		if (status != null) {
	        final Integer statusCode = Integer.valueOf(status.toString());
	     
	        if(statusCode == HttpStatus.BAD_REQUEST.value()) {
	            result = "/400";
	        } else if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
	        	result = "/401";
	        } else if (statusCode == HttpStatus.FORBIDDEN.value()) {
	        	result = "/403";
	        } else if (statusCode == HttpStatus.NOT_FOUND.value()) {
	        	result ="/404";
	        } else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
	        	result = "/500" ;
	        }
	    }
		
		return BASE_ERROR_VIEW + result;
    }
	
	public String getErrorPath() {
		return ERROR_PATH;
	}
}
