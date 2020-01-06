package edu.duke.rs.baseProject.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.security")
public class SecurityProperties {
  private int numberOfLoginAttemptFailuresBeforeTemporaryLock;
  private int temporaryLockSeconds;
  private Password password;
  
  @Getter
  @Setter
  public static class Password {
    private int minLength;
    private int maxLength;
    private int numberLowerCase;
    private int numberUpperCase;
    private int numberDigits;
    private int numberSpecial;
  }
}
