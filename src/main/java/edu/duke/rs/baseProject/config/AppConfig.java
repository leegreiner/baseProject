package edu.duke.rs.baseProject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;
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
  
  // Needed to get Spring Session and SAML to work without error.
  @Bean
  public CookieSerializer cookieSerializer() {
    DefaultCookieSerializer serializer = new DefaultCookieSerializer();
    serializer.setSameSite(null);
    return serializer;
  }
  
  @Bean
  public GoogleProperties googlePropeties() {
    return this.googleProperties;
  }
}
