package edu.duke.rs.baseProject.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MicrometerConfig {
  @Value("${spring.application.name}")
  private String applicationName;
  
  @Bean
  MeterRegistryCustomizer<MeterRegistry> initializeRegistry() {
    return registry -> registry.config().commonTags("application", applicationName);
  }
  
  @Bean
  public TimedAspect timedAspect(final MeterRegistry registry) {
      return new TimedAspect(registry);
  }
}
