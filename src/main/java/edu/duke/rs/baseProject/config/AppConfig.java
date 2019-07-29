package edu.duke.rs.baseProject.config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class AppConfig {
  private transient Environment environment;
  
  public AppConfig(final Environment environment ) {
    this.environment = environment;
  }
  
  @Bean
  public MessageSource messageSource() {
      ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();

      messageSource.setBasename(environment.getRequiredProperty("spring.messages.basename"));
      messageSource.setUseCodeAsDefaultMessage(true);
      messageSource.setDefaultEncoding("UTF-8");
      return messageSource;
  }
  
  @Bean(name = "validator")
  public LocalValidatorFactoryBean validator() {
    final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.setValidationMessageSource(messageSource());
      return validator;
  }
}
