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
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;

import edu.duke.rs.baseProject.StubServletOutputStream;

public class RestBasicAuthentictionEntryPointUnitTest {
  @Mock
  private HttpServletRequest httpServletRequest;
  @Mock
  private HttpServletResponse httpServletResponse;
  private static final RestBasicAuthenticationEntryPoint HANDLER = new RestBasicAuthenticationEntryPoint();
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenAuthenticationFails_thenUnauthorizedIsReturnedWithError() throws Exception {
    final StubServletOutputStream outputStream = new StubServletOutputStream();
    when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("/"));
    when(httpServletResponse.getOutputStream()).thenReturn(outputStream);
    
    HANDLER.commence(httpServletRequest, httpServletResponse, new AuthenticationCredentialsNotFoundException("Oops"));
    
    verify(httpServletResponse, times(1)).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    verify(httpServletResponse, times(1)).getOutputStream();
    verifyNoMoreInteractions(httpServletResponse);
    assertThat(outputStream.toString(), containsString("error"));
    assertThat(outputStream.toString(), containsString("message"));
  }
}
