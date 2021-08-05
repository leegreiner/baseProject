package edu.duke.rs.baseProject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@Configuration
public class AppConfig {
  private transient Environment environment;
  @Autowired
  private GoogleProperties googleProperties;
  
  public AppConfig(final Environment environment) {
    this.environment = environment;
  }
  
  @Bean
  public MessageSource messageSource() {
    final ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource ();

    messageSource.setBasename(environment.getRequiredProperty("spring.messages.basename"));
    messageSource.setDefaultEncoding("UTF-8");
    messageSource.setUseCodeAsDefaultMessage(true);
    return messageSource;
  }
  
  @Bean(name = "validator")
  public LocalValidatorFactoryBean validator() {
    final LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
    validator.setValidationMessageSource(messageSource());
      return validator;
  }
  
  @Bean
  public GoogleProperties googlePropeties() {
    return this.googleProperties;
  }
}
