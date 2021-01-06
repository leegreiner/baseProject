package edu.duke.rs.baseProject.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureLockedEvent;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AuthenticationFailureLockedEventListener implements ApplicationListener<AuthenticationFailureLockedEvent> {
private transient final LoginAttemptService loginAttemptService;
  
  public AuthenticationFailureLockedEventListener(final LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }
  
  @Override
  public void onApplicationEvent(final AuthenticationFailureLockedEvent event) {
    log.debug("Authentication failed for {} (locked)", () -> event.getAuthentication().getName());
    loginAttemptService.loginFailed(event.getAuthentication().getName());
  }
}
