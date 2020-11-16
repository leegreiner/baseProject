package edu.duke.rs.baseProject.config;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.hazelcast.core.HazelcastInstance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Class used to reduce the number of Tomcat shutdown errors about threads and
 * memory leaks. Requires @ServletComponentScan.
 * 
 * @author Lee Greiner
 *
 */
@Slf4j
@RequiredArgsConstructor
@WebListener
public class CustomServletContextListener implements ServletContextListener {
  private final HazelcastInstance hazelcastInstance;
  
  @Override
  public void contextDestroyed(final ServletContextEvent sce) {
    log.debug("Servlet context destruction detected");
    
    final Enumeration<Driver> drivers = DriverManager.getDrivers();
    
    while (drivers.hasMoreElements()) {
        final Driver driver = drivers.nextElement();
        
        try {
            DriverManager.deregisterDriver(driver);
            log.debug(String.format("Deregistering jdbc driver: %s", driver));
        } catch (final SQLException sqlException) {
            log.error(String.format("Error deregistering driver %s", driver), sqlException);
        }
    }
    
    this.hazelcastInstance.shutdown();
  }
}
