package edu.duke.rs.baseProject.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RestBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
  private ObjectMapper mapper = new ObjectMapper();
  
  @Override
  public void commence(HttpServletRequest request, HttpServletResponse response,
      AuthenticationException authException) throws IOException {
    final Map<String, Object> data = new HashMap<>();
    data.put("error", new ErrorInfo(authException.getMessage(), request.getRequestURL().toString()));
    
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.getOutputStream().println(mapper.writeValueAsString(data));
  }
  
  @Override
  public void afterPropertiesSet() {
    setRealmName("management realm");
    super.afterPropertiesSet();
  }
}
