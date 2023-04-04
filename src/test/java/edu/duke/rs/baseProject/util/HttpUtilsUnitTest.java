package edu.duke.rs.baseProject.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

public class HttpUtilsUnitTest {
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpSession httpSession;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenAjaxHeaderNotPresent_thenIsAjaxRequestIsFalse() {
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(null);
    when(request.getHeader(HttpUtils.ACCEPT_HEADER)).thenReturn(null);
    when(request.getLocale()).thenReturn(Locale.US);
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(false));
  }
  
  @Test
  public void whenAjaxHeaderPresentAndNotAjaxValue_thenIsAjaxRequestIsFalse() {
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn("oops");
    when(request.getHeader(HttpUtils.ACCEPT_HEADER)).thenReturn(null);
    when(request.getLocale()).thenReturn(Locale.US);
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(false));
    
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(null);
    when(request.getHeader(HttpUtils.ACCEPT_HEADER)).thenReturn("oops");
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(false));
  }
  
  @Test
  public void whenAjaxHeaderPresentAndNotAjax_thenIsAjaxRequestIsFalse() {
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(HttpUtils.AJAX_REQUEST_HEADER_VALUE);
    when(request.getHeader(HttpUtils.ACCEPT_HEADER)).thenReturn(null);
    when(request.getLocale()).thenReturn(Locale.US);
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(true));
    
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(null);
    when(request.getHeader(HttpUtils.ACCEPT_HEADER)).thenReturn("application/json");
    when(request.getLocale()).thenReturn(Locale.US);
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(true));
    
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(HttpUtils.AJAX_REQUEST_HEADER_VALUE);
    when(request.getHeader(HttpUtils.ACCEPT_HEADER)).thenReturn("application/json");
    when(request.getLocale()).thenReturn(Locale.US);
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(true));
  }
  
  @Test
  public void whenNotifySessionOfChange_thenSecurityContextUpdated() {
    HttpUtils.notifySessionOfChange(httpSession);
    
    verify(httpSession, times(1))
      .setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), any(SecurityContext.class));
  }
}
