package edu.duke.rs.baseProject.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
  private transient final LoginAttemptService loginAttemptService;
  
  public AuthenticationSuccessEventListener(final LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }
  
  @Override
  public void onApplicationEvent(AuthenticationSuccessEvent event) {
    log.debug("Authentication succeeded for " + event.getAuthentication().getName());
    loginAttemptService.loginSucceeded(event.getAuthentication().getName());
  }

}
