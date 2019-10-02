package edu.duke.rs.baseProject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "app.jobs")
public class JobProperties {
  private String expirePasswordChangeIdsCronSchedule;
}
