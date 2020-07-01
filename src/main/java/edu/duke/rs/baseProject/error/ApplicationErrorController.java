package edu.duke.rs.baseProject.error;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.duke.rs.baseProject.BaseWebController;
import io.micrometer.core.instrument.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class ApplicationErrorController extends BaseWebController implements ErrorController {
	public static final String ERROR_MAPPING = "/error";
	static final String BASE_ERROR_VIEW = "error";
	static final String ERROR_VIEW_PARAM = "error";

	@GetMapping(ERROR_MAPPING)
	public String handleError(final HttpServletRequest request) {
	  log.error("In handleError(): " + request.toString());
		final Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		String result = "/unknownError";
		
		if (status == null) {
		  // one scenario when status is null is when we are redirecting from the
		  //  custom AuthenticationFailureHandler
		  final String error = request.getParameter(ERROR_VIEW_PARAM);
      
      if (StringUtils.isNotBlank(error)) {
        result = "/" + error;
      }
		} else {
      final Integer statusCode = Integer.valueOf(status.toString());
   
      if (statusCode == HttpStatus.BAD_REQUEST.value()) {
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
		return ERROR_MAPPING;
	}
}
