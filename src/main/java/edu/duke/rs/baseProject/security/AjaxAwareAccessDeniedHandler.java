package edu.duke.rs.baseProject.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.duke.rs.baseProject.util.HttpUtils;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AjaxAwareAccessDeniedHandler implements AccessDeniedHandler {
  private ObjectMapper mapper = new ObjectMapper();
  private String accessDeniedUrl;
  private String invalidCsrfTokenUrl;
  
  public AjaxAwareAccessDeniedHandler(final String accessDeniedUrl, final String invalidCsrfTokenUrl) {
    this.accessDeniedUrl = accessDeniedUrl;
    this.invalidCsrfTokenUrl = invalidCsrfTokenUrl;
  }
  
  @Override
  public void handle(HttpServletRequest request, HttpServletResponse response,
      AccessDeniedException accessDeniedException) throws IOException, ServletException {
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    
    if (HttpUtils.isAjaxRequest(request)) {
      final Map<String, Object> data = new HashMap<>();
      data.put("error", new ErrorInfo(accessDeniedException.getMessage(), request.getRequestURL().toString()));
      
      response.getOutputStream().println(mapper.writeValueAsString(data));
    } else {
      if (accessDeniedException instanceof InvalidCsrfTokenException ||
          accessDeniedException instanceof MissingCsrfTokenException) {
        response.sendRedirect(request.getContextPath() + invalidCsrfTokenUrl);
      } else {
        response.sendRedirect(request.getContextPath() + accessDeniedUrl);
      }
    }
  }
}
