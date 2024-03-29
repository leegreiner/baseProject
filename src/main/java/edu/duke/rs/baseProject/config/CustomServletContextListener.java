package edu.duke.rs.baseProject.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.context.annotation.Profile;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.hazelcast.core.HazelcastInstance;
import com.zaxxer.hikari.HikariDataSource;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

/**
 * Class used to reduce the number of Tomcat shutdown errors about threads and
 * memory leaks. Requires @ServletComponentScan.
 * 
 * @author Lee Greiner
 *
 */
@Log4j2
@Profile("!local")
@RequiredArgsConstructor
@WebListener
public class CustomServletContextListener implements ServletContextListener {
  private Map<String, HazelcastInstance> hazelcastInstances;
  private Map<String, DataSource> datasources;
  
  @Override
  public void contextInitialized(final ServletContextEvent event) {
    final WebApplicationContext springContext = WebApplicationContextUtils.getWebApplicationContext(event.getServletContext());
    
    if (springContext != null) {
      hazelcastInstances = springContext.getBeansOfType(HazelcastInstance.class);
      datasources = springContext.getBeansOfType(DataSource.class);
    }
  }
  
  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    this.datasources.forEach((key, dataSource) -> ((HikariDataSource) dataSource).close());
    
    final Enumeration<Driver> drivers = DriverManager.getDrivers();
    
    while (drivers.hasMoreElements()) {
        final Driver driver = drivers.nextElement();
        
        try {
            DriverManager.deregisterDriver(driver);
            log.debug("Deregistering jdbc driver {}", () -> driver);
        } catch (final SQLException sqlException) {
            log.error("Error deregistering driver {}", () -> driver, () -> sqlException);
        }
    }
    
    this.hazelcastInstances.forEach((key, hazelcastInstance) -> hazelcastInstance.shutdown());
  }
}