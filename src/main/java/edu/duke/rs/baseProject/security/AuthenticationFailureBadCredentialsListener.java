package edu.duke.rs.baseProject.security;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.stereotype.Component;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Component
public class AuthenticationFailureBadCredentialsListener implements ApplicationListener<AuthenticationFailureBadCredentialsEvent> {
  private transient final LoginAttemptService loginAttemptService;
  
  public AuthenticationFailureBadCredentialsListener(final LoginAttemptService loginAttemptService) {
    this.loginAttemptService = loginAttemptService;
  }
  
  @Override
  public void onApplicationEvent(final AuthenticationFailureBadCredentialsEvent event) {
    log.debug("Authentication failed for " + event.getAuthentication().getName() + " (bad credentials)");
    loginAttemptService.loginFailed(event.getAuthentication().getName());
  }
}
