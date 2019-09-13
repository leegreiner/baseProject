package edu.duke.rs.baseProject.config;

import java.util.List;

import javax.cache.CacheManager;
import javax.cache.configuration.MutableConfiguration;

import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

// Hibernate 5.4 will have a fix where classpath:ehcache.xml will be successfully parsed.
// Until then we need to disable caching during testing
@Profile("!test")
@Configuration
@EnableCaching
public class CacheConfig implements JCacheManagerCustomizer {
  private static final List<String> CACHES = List.of("edu.duke.rs.baseProject.role.Role");

  @Override
  public void customize(CacheManager cacheManager) {
    CACHES.stream().forEach(cacheName -> 
      cacheManager.createCache(cacheName, new MutableConfiguration<>().setStoreByValue(false)));
  }
}
