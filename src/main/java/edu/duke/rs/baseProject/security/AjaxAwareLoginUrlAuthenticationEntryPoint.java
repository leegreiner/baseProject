package edu.duke.rs.baseProject.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.duke.rs.baseProject.util.HttpUtils;

public class AjaxAwareLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {
  private ObjectMapper mapper = new ObjectMapper();
  
  public AjaxAwareLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
    super(loginFormUrl);
  }
  
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException, ServletException {
    if (HttpUtils.isAjaxRequest(request)) {
      final Map<String, Object> data = new HashMap<>();
      data.put("error", new ErrorInfo(authException.getMessage(), request.getRequestURL().toString()));
      
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
      response.getOutputStream().println(mapper.writeValueAsString(data));
    } else {
      super.commence(request, response, authException);
    }
  }

}
