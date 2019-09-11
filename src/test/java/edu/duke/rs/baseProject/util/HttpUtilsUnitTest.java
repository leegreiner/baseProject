package edu.duke.rs.baseProject.util;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

public class HttpUtilsUnitTest {
  @Mock
  private HttpServletRequest request;
  @Mock
  private HttpSession httpSession;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void whenAjaxHeaderNotPresent_thenIsAjaxRequestIsFalse() {
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(null);
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(false));
  }
  
  @Test
  public void whenAjaxHeaderPresentAndNotAjaxValue_thenIsAjaxRequestIsFalse() {
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn("oops");
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(false));
  }
  
  @Test
  public void whenAjaxHeaderPresentAndNotAjax_thenIsAjaxRequestIsFalse() {
    when(request.getHeader(HttpUtils.AJAX_REQUEST_HEADER)).thenReturn(HttpUtils.AJAX_REQUEST_HEADER_VALUE);
    
    assertThat(HttpUtils.isAjaxRequest(request), equalTo(true));
  }
  
  @Test
  public void whenNotifySessionOfChange_thenSecurityContextUpdated() {
    HttpUtils.notifySessionOfChange(httpSession);
    
    verify(httpSession, times(1))
      .setAttribute(eq(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY), any(SecurityContext.class));
  }
}
