package edu.duke.rs.baseProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.hazelcast.Hazelcast4IndexedSessionRepository;
import org.springframework.session.hazelcast.Hazelcast4PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

import com.hazelcast.config.AttributeConfig;
import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionConfig;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.IndexConfig;
import com.hazelcast.config.IndexType;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizePolicy;

@Configuration
@EnableHazelcastHttpSession(maxInactiveIntervalInSeconds = 3600)
public class HazelcastConfig {
  private static final Integer PARTITION_COUNT = 271;
  private final String namespace;
  private final String discoveryService;
  private final String instanceName;
  private final int multicastPort;
  private final int bruteForceTtlSeconds;
  private final int bruteForceSize;
  
  public HazelcastConfig(final ApplicationProperties applicationProperties) {
    this.namespace = applicationProperties.getKubernetes().getNamespace();
    this.discoveryService = applicationProperties.getKubernetes().getHazelcastDiscoveryService();
    this.instanceName = applicationProperties.getHazelcast().getInstanceName();
    this.multicastPort = applicationProperties.getHazelcast().getMulticastPort();
    this.bruteForceTtlSeconds = applicationProperties.getHazelcast().getBruteForceTtlSeconds();
    this.bruteForceSize = applicationProperties.getHazelcast().getBruteForceSize();
  }
  
  @Bean
  @Profile({"!val & !prod"})
  public Config hazelcastLocalConfig() {
    final Config config = this.hazelcastBaseConfig();
    
    config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(true);
    config.getNetworkConfig().getJoin().getMulticastConfig().setMulticastPort(this.multicastPort);
    
    return config;
  }
  
  @Bean
  @Profile({"val", "prod"})
  public Config hazelcastKubernetesConfig() {
    final Config config = hazelcastBaseConfig();
    
    config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
        config.getNetworkConfig().getJoin().getKubernetesConfig().setEnabled(true)
                .setProperty("namespace", this.namespace)
                .setProperty("service-name", this.discoveryService);
    
    return config;
  }
  
  private Config hazelcastBaseConfig() {
    final Config config = new Config().setInstanceName(this.instanceName);
    
    config.getMapConfig(Hazelcast4IndexedSessionRepository.DEFAULT_SESSION_MAP_NAME)
      .addAttributeConfig(springSessionAttributeConfig()).addIndexConfig(
          new IndexConfig(IndexType.HASH, Hazelcast4IndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE));
    config.addMapConfig(bruteForceLoginMapConfig());
    
    return config;
  }
  
  private AttributeConfig springSessionAttributeConfig() {
    return new AttributeConfig()
        .setName(Hazelcast4IndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
        .setExtractorClassName(Hazelcast4PrincipalNameExtractor.class.getName());
  }
  
  private MapConfig bruteForceLoginMapConfig() {
    return new MapConfig().setName(CacheConfig.BRUTE_FORCE_AUTHENTICATION_CACHE)
        .setTimeToLiveSeconds(this.bruteForceTtlSeconds)
        .setEvictionConfig(new EvictionConfig()
            .setEvictionPolicy(EvictionPolicy.LRU).setMaxSizePolicy(MaxSizePolicy.PER_NODE).setSize(PARTITION_COUNT + this.bruteForceSize));
  }
}