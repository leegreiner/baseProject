package edu.duke.rs.baseProject.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class HttpUtils {
  public static final String ACCEPT_HEADER = "Accept";
  public static final String AJAX_REQUEST_HEADER = "X-Requested-With";
  public static final String AJAX_REQUEST_HEADER_VALUE = "XMLHttpRequest";
  
  private HttpUtils() {}

  public static boolean isAjaxRequest(final HttpServletRequest request) {
    final String  acceptHeader = request.getHeader(ACCEPT_HEADER); 
    final String ajaxHeader = request.getHeader(AJAX_REQUEST_HEADER);
    return (ajaxHeader != null && ajaxHeader.equals(AJAX_REQUEST_HEADER_VALUE)) ||
        (acceptHeader != null && acceptHeader.toLowerCase(request.getLocale()).contains("json"));
  }
  
  /**
   * There are some situation when Spring Session does not detect a change in session attributes.
   * This method forces Spring Session to notice.
   *
   * @param httpSession the httpSession
   */
  public static void notifySessionOfChange(final HttpSession httpSession) {
    httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
  }
}
