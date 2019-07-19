package edu.duke.rs.baseProject.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import edu.duke.rs.baseProject.security.PasswordExpirationStrategy;
import edu.duke.rs.baseProject.security.PasswordNeverExpiresStrategy;

@Configuration
public class AutoConfiguration {
  
  
  @Bean
  @ConditionalOnMissingBean
  public PasswordExpirationStrategy passwordExpirationStrategy() {
    return new PasswordNeverExpiresStrategy();
  }

}
