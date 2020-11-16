package edu.duke.rs.baseProject.config;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
class GracefulTomcatShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
  private static final long TOMCAT_THREAD_TIMEOUT_SECONDS = 60;
  private volatile Connector connector;
  
  @Override
  public void onApplicationEvent(final ContextClosedEvent event) {
    log.debug("ContextClosedEvent received");
    if (connector == null) {
      return;
    }
    
    this.connector.pause();
    
    final Executor executor = this.connector.getProtocolHandler().getExecutor();
    
    if (executor instanceof ThreadPoolExecutor) {
      try {
          ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
          threadPoolExecutor.shutdown();
          if (!threadPoolExecutor.awaitTermination(TOMCAT_THREAD_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
              log.warn("Tomcat thread pool did not shut down gracefully within "
                      + TOMCAT_THREAD_TIMEOUT_SECONDS + " seconds. Proceeding with forceful shutdown");
          }
      } catch (InterruptedException ex) {
          Thread.currentThread().interrupt();
      }
    }
  }

  @Override
  public void customize(final Connector connector) {
    this.connector = connector;
  }
}
