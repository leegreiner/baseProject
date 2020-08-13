package edu.duke.rs.baseProject.config;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.threads.ThreadPoolExecutor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextClosedEvent;

import com.hazelcast.core.HazelcastInstance;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
  @Value("${server.port:8080}")
  private int serverPort;
  @Value("${server.ssl.enabled:false}")
  private boolean sslEnabled;
  private final HazelcastInstance hazelcastInstance;

  @Override
  public void customize(final TomcatServletWebServerFactory factory) {
    factory.addConnectorCustomizers(new RelaxedQueryTomcatCustomizer());
    factory.addConnectorCustomizers(new GracefulShutdown(hazelcastInstance));

    if (sslEnabled) {
      factory.addAdditionalTomcatConnectors(redirectConnector());
    }
  }

  private static class RelaxedQueryTomcatCustomizer implements TomcatConnectorCustomizer {
    @Override
    public void customize(final Connector connector) {
      connector.setProperty("relaxedQueryChars", "|{}[]");
    }
  }

  private Connector redirectConnector() {
    final Connector connector = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setSecure(false);
    connector.setPort(8080);
    connector.setRedirectPort(serverPort);
    return connector;
  }
  
  private static final class GracefulShutdown implements TomcatConnectorCustomizer, ApplicationListener<ContextClosedEvent> {
    private volatile Connector connector;
    private final HazelcastInstance hazelcastInstance;
    
    public GracefulShutdown(final HazelcastInstance hazelcastInstance) {
      this.hazelcastInstance = hazelcastInstance;
    }
    
    @Override
    public void onApplicationEvent(final ContextClosedEvent event) {
      this.connector.pause();
      
      final Executor executor = this.connector.getProtocolHandler().getExecutor();
      
      if (executor instanceof ThreadPoolExecutor) {
          try {
              ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) executor;
              threadPoolExecutor.shutdown();
              if (!threadPoolExecutor.awaitTermination(60, TimeUnit.SECONDS)) {
                  log.warn("Tomcat thread pool did not shut down gracefully within "
                          + "60 seconds. Proceeding with forceful shutdown");
              }
          } catch (InterruptedException ex) {
              Thread.currentThread().interrupt();
          }
      }
      
      hazelcastInstance.shutdown();
    }

    @Override
    public void customize(final Connector connector) {
      this.connector = connector;
    }
  }
}
