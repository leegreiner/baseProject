package edu.duke.rs.baseProject.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationFailureLockedEventListener implements ApplicationListener<AuthenticationFailureLockedEvent> {
private transient final LoginAttemptService loginAttemptService;
  
  public AuthenticationFailureLockedEventListener(final LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }
  
  @Override
  public void onApplicationEvent(final AuthenticationFailureLockedEvent event) {
    log.debug("Authentication failed for " + event.getAuthentication().getName() + " (locked)");
    loginAttemptService.loginFailed(event.getAuthentication().getName());
  }
}
