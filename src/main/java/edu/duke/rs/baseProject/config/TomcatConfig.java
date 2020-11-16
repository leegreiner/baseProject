package edu.duke.rs.baseProject.config;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class TomcatConfig implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
  @Value("${server.port:8080}")
  private int serverPort;
  @Value("${server.ssl.enabled:false}")
  private boolean sslEnabled;
  private final GracefulTomcatShutdown gracefulShutdown;

  @Override
  public void customize(final TomcatServletWebServerFactory factory) {
    factory.addConnectorCustomizers(new RelaxedQueryTomcatCustomizer());
    factory.addConnectorCustomizers(gracefulShutdown);

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
  
  @Bean
  public static GracefulTomcatShutdown gracefulShutdown() {
    return new GracefulTomcatShutdown();
  }
}
