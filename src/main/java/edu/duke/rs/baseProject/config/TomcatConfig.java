package edu.duke.rs.baseProject.config;

import org.apache.catalina.Context;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.descriptor.web.SecurityCollection;
import org.apache.tomcat.util.descriptor.web.SecurityConstraint;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TomcatConfig {
  @Value("${server.port}")
  private int serverPort;
  
  /*
  @Bean
  public ConfigurableServletWebServerFactory webServerFactory() {
    final TomcatServletWebServerFactory factory = new TomcatServletWebServerFactory();
    factory.addConnectorCustomizers(new CustomTomcatConnectorCustomizer());
    
    return factory;
  }
  */
  
  @Bean
  public ServletWebServerFactory servletContainer() {
      TomcatServletWebServerFactory tomcat = new TomcatServletWebServerFactory() {
          @Override
          protected void postProcessContext(Context context) {
              SecurityConstraint securityConstraint = new SecurityConstraint();
              securityConstraint.setUserConstraint("CONFIDENTIAL");
              SecurityCollection collection = new SecurityCollection();
              collection.addPattern("/*");
              securityConstraint.addCollection(collection);
              context.addConstraint(securityConstraint);
          }
      };
      tomcat.addConnectorCustomizers(new RelaxedQueryTomcatCustomizer());
      tomcat.addAdditionalTomcatConnectors(redirectConnector());
      return tomcat;
  }

  
  private static class RelaxedQueryTomcatCustomizer implements TomcatConnectorCustomizer {
    @Override
    public void customize(final Connector connector) {
        connector.setProperty("relaxedQueryChars", "|{}[]");
    }
  }
  
  private Connector redirectConnector() {
    Connector connector
            = new Connector("org.apache.coyote.http11.Http11NioProtocol");
    connector.setScheme("http");
    connector.setSecure(false);
    connector.setPort(8080);
    connector.setRedirectPort(serverPort);
    return connector;
 }
}
