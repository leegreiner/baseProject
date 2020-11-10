package edu.duke.rs.baseProject;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestUtilsService {
  private static final String AUDIT_TABLE_NAME_SUFFIX = "_aud";
  private static final List<String> AUDITED_TABLES = List.of(
      "users_to_roles",
      "password_history",
      "users",
      "role");
  private static final List<String> UNAUDITED_TABLES = List.of(
      "revchanges",
      "audit_revision_entity");
  
  private final EntityManager entityManager;
  private final CacheManager cacheManager;
  
  public TestUtilsService(final EntityManager entityManager, @Nullable final CacheManager cacheManager) {
    this.entityManager = entityManager;
    this.cacheManager = cacheManager;
  }

  @Transactional
  public void resetState() throws Exception {
    cleanAllDatabases();
    cleanAllCaches();
  }
  
  private void cleanAllDatabases() throws Exception {
    this.entityManager.clear();
    
    // for h2 only
    this.entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY FALSE").executeUpdate();
    
    UNAUDITED_TABLES.stream().forEach(tableName -> this.entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate());
    
    AUDITED_TABLES.stream().forEach(tableName -> {
      this.entityManager.createNativeQuery("TRUNCATE TABLE " + tableName + AUDIT_TABLE_NAME_SUFFIX).executeUpdate();
      this.entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate();
    });

    // for h2 only
    this.entityManager.createNativeQuery("SET REFERENTIAL_INTEGRITY TRUE").executeUpdate();
  }

  private void cleanAllCaches() {
    if (this.cacheManager != null) {
      this.cacheManager.getCacheNames()
        .stream()
        .map(it -> cacheManager.getCache(it))
        .filter(it -> it != null)
        .forEach(it -> it.clear());
    }
  }
}