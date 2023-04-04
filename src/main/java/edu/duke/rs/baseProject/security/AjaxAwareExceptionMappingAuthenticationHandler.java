package edu.duke.rs.baseProject.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.duke.rs.baseProject.util.HttpUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AjaxAwareExceptionMappingAuthenticationHandler extends ExceptionMappingAuthenticationFailureHandler  {
  private ObjectMapper mapper = new ObjectMapper();
  
  @Override
  public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response,
      final AuthenticationException exception) throws IOException, ServletException {
    if (HttpUtils.isAjaxRequest(request)) {
      final Map<String, Object> data = new HashMap<>();
      data.put("error", new ErrorInfo(exception.getMessage(), request.getRequestURL().toString()));
      
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getOutputStream().println(mapper.writeValueAsString(data));
    } else {
      super.onAuthenticationFailure(request, response, exception);
    }
  }
}
