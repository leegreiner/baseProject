package edu.duke.rs.baseProject.config;

import java.util.Collections;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;

@Configuration
public class SpringFoxConfig {
  private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = Set.of("application/json");
  @Value("${info.app.version}")
  private String applicationVersion;
  
  @Bean
  public Docket api() {
    return new Docket(DocumentationType.SWAGGER_2)
        .select()
        .apis(RequestHandlerSelectors.withClassAnnotation(org.springframework.web.bind.annotation.RestController.class))
        .paths(PathSelectors.any())
        .build()
        .apiInfo(apiInfo())
        .produces(DEFAULT_PRODUCES_AND_CONSUMES)
        .consumes(DEFAULT_PRODUCES_AND_CONSUMES);
  }

  private ApiInfo apiInfo() {
    return new ApiInfo("Baseapp REST API", "Rest API for Baseapp", applicationVersion, "urn:tos",
        new Contact("Lee Greiner", "localhost:8080", "lmg@yahoo.com"), "License of API", "API license URL",
        Collections.emptyList());
  }
}
