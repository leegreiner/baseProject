package edu.duke.rs.baseProject.config;

import java.util.stream.Stream;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.jdbc.support.DatabaseStartupValidator;

@Configuration
@EnableJpaAuditing
public class JpaConfig {
  @Value("${spring.datasource.hikari.connection-test-query:select 1 from dual}")
  private String validationQuery;
  @Value("${app.datasource.startupValidationInterval}")
  private int startupValidationInterval;
  @Value("${app.datasource.startupValidationTimeout}")
  private int startupValidationTimeout;
  
  @Bean
  public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
      final DatabaseStartupValidator dsv= new DatabaseStartupValidator();
      dsv.setDataSource(dataSource);
      dsv.setInterval(startupValidationInterval);
      dsv.setTimeout(startupValidationTimeout);
      dsv.setValidationQuery(validationQuery);
      return dsv;
  }
  
  // delay application startup until database is available
  @Bean
  public static BeanFactoryPostProcessor databaseStartupValidationPostProcessor() {
    return bf -> {
      final String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);
      
      Stream.of(jpa)
        .map(bf::getBeanDefinition)
        .forEach(it -> it.setDependsOn("databaseStartupValidator"));
    };
  }
}
