package edu.duke.rs.baseProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;

@Configuration
public class MicrometerConfig {
  @Bean
  public TimedAspect timedAspect(final MeterRegistry registry) {
      return new TimedAspect(registry);
  }
}
