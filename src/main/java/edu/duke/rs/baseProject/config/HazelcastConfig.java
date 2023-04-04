package edu.duke.rs.baseProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

import com.hazelcast.config.AttributeConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;

@Configuration
@EnableHazelcastHttpSession(maxInactiveIntervalInSeconds = 3600)
public class HazelcastConfig {
  private final String namespace;
  private final String hazelcastDiscoveryService;
  private final String instanceName;
  private final int hazelcastMulticastPort;
  
  public HazelcastConfig(final ApplicationProperties applicationProperties) {
    this.namespace = applicationProperties.getKubernetes().getNamespace();
    this.hazelcastDiscoveryService = applicationProperties.getKubernetes().getHazelcastDiscoveryService();
    this.hazelcastMulticastPort = applicationProperties.getHazelcast().getMulticastPort();
    this.instanceName = applicationProperties.getHazelcast().getInstanceName();
  }
  
  @Bean
  @Profile({"!val & !prod"})
  Config hazelcastLocalConfig() {
    final Config config = this.hazelcastBaseConfig();
    
    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
    config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastPort(this.hazelcastMulticastPort);
    
    return config;
  }
  
  @Bean
  @Profile({"val", "prod"})
  Config hazelcastKubernetesConfig() {
    final Config config = hazelcastBaseConfig();
    
    config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(true)
                .setProperty("namespace", this.namespace)
                .setProperty("service-name", this.hazelcastDiscoveryService);
    
    return config;
  }
  
  private Config hazelcastBaseConfig() {
    final Config config = new Config().setInstanceName(this.instanceName);
    
    config.getMapConfig(HazelcastIndexedSessionRepository.DEFAULT_SESSION_MAP_NAME)
      .addAttributeConfig(attributeConfig()).addIndexConfig(
        new IndexConfig(IndexType.HASH, HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE));
    
    return config;
  }
  
  private AttributeConfig attributeConfig() {
    return new AttributeConfig()
        .setName(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
        .setExtractorClassName(PrincipalNameExtractor.class.getName());
  }
}