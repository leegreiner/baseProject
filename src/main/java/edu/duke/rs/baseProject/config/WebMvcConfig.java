package edu.duke.rs.baseProject.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import edu.duke.rs.baseProject.formatters.LocalDateTimeFormatter;
import edu.duke.rs.baseProject.role.RolesFormatter;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer  {
  @Override
  public void addFormatters(FormatterRegistry registry) {
    registry.addFormatter(new RolesFormatter());
    registry.addFormatter(new LocalDateTimeFormatter());
  }
}
