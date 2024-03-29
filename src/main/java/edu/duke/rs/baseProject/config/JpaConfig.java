package edu.duke.rs.baseProject.config;

import java.util.stream.Stream;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.support.DatabaseStartupValidator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import edu.duke.rs.baseProject.repository.ExtendedJpaRepositoryImpl;
import jakarta.persistence.EntityManagerFactory;

@Configuration
@EnableJpaRepositories(basePackages = "edu.duke.rs.baseProject", repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
@EnableJpaAuditing
public class JpaConfig {
  @Value("${app.datasource.startupValidationInterval:5}")
  private int startupValidationInterval;
  @Value("${app.datasource.startupValidationTimeout:60}")
  private int startupValidationTimeout;

  @Bean
  public DatabaseStartupValidator databaseStartupValidator(DataSource dataSource) {
    final DatabaseStartupValidator dsv = new DatabaseStartupValidator();
    dsv.setDataSource(dataSource);
    dsv.setInterval(startupValidationInterval);
    dsv.setTimeout(startupValidationTimeout);
    return dsv;
  }

  // delay application startup until database is available
  @Bean
  public static BeanFactoryPostProcessor databaseStartupValidationPostProcessor() {
    return bf -> {
      final String[] jpa = bf.getBeanNamesForType(EntityManagerFactory.class);

      Stream.of(jpa).map(bf::getBeanDefinition).forEach(it -> it.setDependsOn("databaseStartupValidator"));
    };
  }

  @Bean
  public PlatformTransactionManager transactionManager(
      final LocalContainerEntityManagerFactoryBean entityManagerFactoryBean) {
    final JpaTransactionManager transactionManager = new AuditingJpaTransactionManager();
    transactionManager.setEntityManagerFactory(entityManagerFactoryBean.getObject());
    return transactionManager;
  }
}
