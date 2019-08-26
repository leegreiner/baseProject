package edu.duke.rs.baseProject.config;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.web.client.RestTemplate;

import de.codecentric.boot.admin.client.config.ClientProperties;
import de.codecentric.boot.admin.client.registration.ApplicationFactory;
import de.codecentric.boot.admin.client.registration.ApplicationRegistrator;

@Configuration
public class AdminServerConfig {
  @Autowired
  private ClientProperties admin;
  @Autowired
  private ApplicationFactory applicationFactory;
  
  @Bean
  public ApplicationRegistrator getCustomApplicationRegistrator(){
      final RestTemplate restTemplate = getCustomRestTemplate();
      restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(admin.getUsername(), admin.getPassword()));
      
      return new ApplicationRegistrator(restTemplate, admin, applicationFactory);
  }
  
  private static RestTemplate getCustomRestTemplate() {
    //TODO uncomment when wildcard cert issued
    //final PublicSuffixMatcher suffixMatcher = new PublicSuffixMatcher(List.of("*.drc.duke.edu"), Lists.emptyList());
    final SSLContext sslContext = org.apache.http.ssl.SSLContexts.createDefault();
    //final SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new DefaultHostnameVerifier(suffixMatcher));
    final SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());
    final CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(csf).build();
    
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setHttpClient(httpClient);
    RestTemplate restTemplate = new RestTemplate(requestFactory);
    return restTemplate;
  }
}
