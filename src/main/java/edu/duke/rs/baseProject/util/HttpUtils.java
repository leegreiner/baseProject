package edu.duke.rs.baseProject.util;

import javax.servlet.http.HttpServletRequest;

public class HttpUtils {
  public static final String AJAX_REQUEST_HEADER = "X-Requested-With";
  public static final String AJAX_REQUEST_HEADER_VALUE = "XMLHttpRequest";
  
  private HttpUtils() {}

  public static boolean isAjaxRequest(final HttpServletRequest request) {
    final String ajaxHeader = request.getHeader(AJAX_REQUEST_HEADER);
    return ajaxHeader == null ? false : ajaxHeader.equals(AJAX_REQUEST_HEADER_VALUE);
  }
}
