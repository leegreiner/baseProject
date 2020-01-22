package edu.duke.rs.baseProject.security;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.security.core.Authentication;

public class AuthenticationFailureLockedEventListenerUnitTest {
  @Mock
  private LoginAttemptServiceImpl loginAttemptService;
  @Mock
  private AuthenticationFailureLockedEvent event;
  @Mock
  private Authentication authentication;
  private AuthenticationFailureLockedEventListener listener;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    listener = new AuthenticationFailureLockedEventListener(loginAttemptService);
  }
  
  @Test
  public void whenBadCredentailEventReceived_thenLoginAttemptServiceCalled() {
    final String userName = "abc";
    when(authentication.getName()).thenReturn(userName);
    when(event.getAuthentication()).thenReturn(authentication);
    
    listener.onApplicationEvent(event);
    
    verify(loginAttemptService, times(1)).loginFailed(userName);
    verify(event, times(2)).getAuthentication();
    verify(authentication, times(2)).getName();
    verifyNoMoreInteractions(loginAttemptService);
    verifyNoMoreInteractions(event);
    verifyNoMoreInteractions(authentication);
  }
}
