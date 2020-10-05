package edu.duke.rs.baseProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.session.hazelcast.HazelcastIndexedSessionRepository;
import org.springframework.session.hazelcast.PrincipalNameExtractor;
import org.springframework.session.hazelcast.config.annotation.web.http.EnableHazelcastHttpSession;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapAttributeConfig;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MapIndexConfig;
import com.hazelcast.config.MaxSizeConfig;

@Configuration
@EnableHazelcastHttpSession(maxInactiveIntervalInSeconds = 3600)
public class HazelcastConfig {
  private static final Integer BASE_MAP_SIZE = 271;
  private final String namespace;
  private final String hazelcastDiscoveryService;
  private final String instanceName;
  
  public HazelcastConfig(final ApplicationProperties applicationProperties) {
    this.namespace = applicationProperties.getKubernetes().getNamespace();
    this.hazelcastDiscoveryService = applicationProperties.getKubernetes().getHazelcastDiscoveryService();
    this.instanceName = applicationProperties.getHazelcast().getInstanceName();
  }
  
  @Bean
  @Profile({"!val & !prod"})
  public Config hazelcastLocalConfig() {
    return this.hazelcastBaseConfig();
  }
  
  @Bean
  @Profile({"val", "prod"})
  public Config hazelcastKubernetesConfig() {
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
      .addMapAttributeConfig(springSessionAttributeConfig()).addMapIndexConfig(
        new MapIndexConfig(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE, false));
    
    config.addMapConfig(roleMapConfig());
    
    return config;
  }
  
  private MapAttributeConfig springSessionAttributeConfig() {
    return new MapAttributeConfig()
        .setName(HazelcastIndexedSessionRepository.PRINCIPAL_NAME_ATTRIBUTE)
        .setExtractor(PrincipalNameExtractor.class.getName());
  }
  
  private MapConfig roleMapConfig() {
    return new MapConfig().setName("role")
        .setTimeToLiveSeconds(0)
        .setMaxSizeConfig(new MaxSizeConfig(BASE_MAP_SIZE + 500, MaxSizeConfig.MaxSizePolicy.PER_NODE))
        .setEvictionPolicy(EvictionPolicy.LRU);
  }
}
