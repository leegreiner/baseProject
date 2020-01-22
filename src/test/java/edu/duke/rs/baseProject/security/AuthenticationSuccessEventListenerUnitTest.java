package edu.duke.rs.baseProject.security;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;

public class AuthenticationSuccessEventListenerUnitTest {
  @Mock
  private LoginAttemptServiceImpl loginAttemptService;
  @Mock
  private AuthenticationSuccessEvent event;
  @Mock
  private Authentication authentication;
  private AuthenticationSuccessEventListener listener;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    listener = new AuthenticationSuccessEventListener(loginAttemptService);
  }
  
  @Test
  public void whenSuccessEventReceived_thenLoginAttemptServiceCalled() {
    final String userName = "abc";
    when(authentication.getName()).thenReturn(userName);
    when(event.getAuthentication()).thenReturn(authentication);
    
    listener.onApplicationEvent(event);
    
    verify(loginAttemptService, times(1)).loginSucceeded(userName);
    verify(event, times(2)).getAuthentication();
    verify(authentication, times(2)).getName();
    verifyNoMoreInteractions(loginAttemptService);
    verifyNoMoreInteractions(event);
    verifyNoMoreInteractions(authentication);
  }
}
