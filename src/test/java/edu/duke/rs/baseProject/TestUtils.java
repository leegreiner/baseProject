package edu.duke.rs.baseProject;

import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;

public class TestUtils {
  private static final String AUDIT_TABLE_NAME_SUFFIX = "_aud";
  private static final List<String> AUDITED_TABLES = List.of(
      "users_to_roles",
      "password_history",
      "users",
      "role");
  private static final List<String> UNAUDITED_TABLES = List.of(
      "revchanges",
      "audit_revision_entity");
  
  private TestUtils() {}
  
  public static void resetState(final JdbcTemplate jdbcTemplate, final CacheManager cacheManager) throws Exception {
    cleanAllDatabases(jdbcTemplate);
    cleanAllCaches(cacheManager);
  }
  
  private static void cleanAllDatabases(final JdbcTemplate jdbcTemplate) throws Exception {
    // for h2 only
    jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY FALSE");
    
    UNAUDITED_TABLES.stream().forEach(tableName -> jdbcTemplate.execute("TRUNCATE TABLE " + tableName));
    
    AUDITED_TABLES.stream().forEach(tableName -> {
      jdbcTemplate.execute("TRUNCATE TABLE " + tableName + AUDIT_TABLE_NAME_SUFFIX);
      jdbcTemplate.execute("TRUNCATE TABLE " + tableName);
    });

    // for h2 only
    jdbcTemplate.execute("SET REFERENTIAL_INTEGRITY TRUE");
  }

  private static void cleanAllCaches(final CacheManager cacheManager) {
    if (cacheManager != null) {
      cacheManager.getCacheNames()
        .stream()
        .map(it -> cacheManager.getCache(it))
        .filter(it -> it != null)
        .forEach(it -> it.clear());
    }
  }
}
