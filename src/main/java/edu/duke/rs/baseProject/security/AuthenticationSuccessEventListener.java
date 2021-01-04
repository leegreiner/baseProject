package edu.duke.rs.baseProject.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
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
