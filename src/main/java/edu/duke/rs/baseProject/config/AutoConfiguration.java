package edu.duke.rs.baseProject.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.duke.rs.baseProject.security.password.PasswordExpirationStrategy;
import edu.duke.rs.baseProject.security.password.PasswordNeverExpiresStrategy;
import edu.duke.rs.baseProject.user.passwordReset.DoNothingPasswordResetService;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetService;

@Configuration
public class AutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public PasswordExpirationStrategy passwordExpirationStrategy() {
    return new PasswordNeverExpiresStrategy();
  }
  
  @Bean
  @ConditionalOnMissingBean
  public PasswordResetService passwordResetService() {
    return new DoNothingPasswordResetService();
  }

}
