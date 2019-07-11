package edu.duke.rs.baseProject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.google")
public class GoogleProperties {
  private String analysicsTrackingId;
}
