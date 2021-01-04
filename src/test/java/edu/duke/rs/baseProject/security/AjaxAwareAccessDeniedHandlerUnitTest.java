package edu.duke.rs.baseProject.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.DefaultCsrfToken;
import org.springframework.security.web.csrf.InvalidCsrfTokenException;
import org.springframework.security.web.csrf.MissingCsrfTokenException;

import edu.duke.rs.baseProject.StubServletOutputStream;
import edu.duke.rs.baseProject.util.HttpUtils;

public class AjaxAwareAccessDeniedHandlerUnitTest {
  @Mock
  private HttpServletRequest httpServletRequest;
  @Mock
  private HttpServletResponse httpServletResponse;
  private static final String ACCESS_DENIED_URL = "/accessDenied";
  private static final String INVALID_CSRF_URL = "/invalidCsrf";
  private static final AjaxAwareAccessDeniedHandler HANDLER = new AjaxAwareAccessDeniedHandler(ACCESS_DENIED_URL, INVALID_CSRF_URL);
  private static final CsrfToken EXPECTED_CSRF_TOKEN = new DefaultCsrfToken("header", "parameter", "token"); 
  private static final String ACTUAL_TOKEN = "token3"; 
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenIsAjaxRequest_thenResponseBodyContainsException() throws Exception {
    final StubServletOutputStream outputStream = new StubServletOutputStream();
    when(httpServletRequest.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(HttpUtils.AJAX_REQUEST_HEADER_VALUE);
    when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/"));
    when(httpServletResponse.getOutputStream()).thenReturn(outputStream);
    
    HANDLER.handle(httpServletRequest, httpServletResponse, new InvalidCsrfTokenException(EXPECTED_CSRF_TOKEN, ACTUAL_TOKEN));
    
    verify(httpServletResponse, times(1)).setStatus(HttpServletResponse.SC_FORBIDDEN);
    verify(httpServletResponse, times(1)).getOutputStream();
    verifyNoMoreInteractions(httpServletResponse);
    assertThat(outputStream.toString(), containsString("error"));
    assertThat(outputStream.toString(), containsString("message"));
  }
  
  @Test
  public void whenIsNotAjaxRequestAndIsInvalidCsrfTokenException_thenRedirectedToInvalidCsrfUrl() throws Exception {
    when(httpServletRequest.getContextPath()).thenReturn("/app");
    
    HANDLER.handle(httpServletRequest, httpServletResponse, new InvalidCsrfTokenException(EXPECTED_CSRF_TOKEN, ACTUAL_TOKEN));
    
    verify(httpServletResponse, times(1)).sendRedirect("/app" + INVALID_CSRF_URL);
  }
  
  @Test
  public void whenIsNotAjaxRequestAndIsMissingCsrfTokenException_thenRedirectedToInvalidCsrfUrl() throws Exception {
    when(httpServletRequest.getContextPath()).thenReturn("/app");
    
    HANDLER.handle(httpServletRequest, httpServletResponse, new MissingCsrfTokenException(ACTUAL_TOKEN));
    
    verify(httpServletResponse, times(1)).sendRedirect("/app" + INVALID_CSRF_URL);
  }

  @Test
  public void whenIsNotAjaxRequestAndIsNotInvalidCsrfException_thenRedirectedToInvalidAccessDeniedUrl() throws Exception {
    when(httpServletRequest.getContextPath()).thenReturn("/app");
    
    HANDLER.handle(httpServletRequest, httpServletResponse, new AccessDeniedException("Oops"));
    
    verify(httpServletResponse, times(1)).sendRedirect("/app" + ACCESS_DENIED_URL);
  }
}
