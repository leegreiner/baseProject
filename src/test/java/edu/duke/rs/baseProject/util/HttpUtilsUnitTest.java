package edu.duke.rs.baseProject.util;

import javax.servlet.http.HttpServletRequest;
import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HttpUtilsUnitTest {
  @Mock
  private HttpServletRequest request;
  
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
}
