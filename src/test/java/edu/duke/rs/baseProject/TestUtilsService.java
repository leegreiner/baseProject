package edu.duke.rs.baseProject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.springframework.cache.CacheManager;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TestUtilsService {
  private static final List<String> EXCLUDED_TABLES = List.of("DATABASECHANGELOG","DATABASECHANGELOGLOCK");
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
    
    this.getTableNames()
      .forEach(tableName -> this.entityManager.createNativeQuery("TRUNCATE TABLE " + tableName).executeUpdate());

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
  
  private List<String> getTableNames() {
    return this.entityManager.unwrap(Session.class).doReturningWork(new ReturningWork<List<String>>() {
      @Override
      public List<String> execute(final Connection connection) throws SQLException {
        final ResultSet tableRefs = connection.getMetaData()
            .getTables(connection.getCatalog(), null,null, List.of("TABLE").toArray(new String[0]));
        final List<String> tableNames = new ArrayList<String>();
        
        while(tableRefs.next()) {
          final String tableName = tableRefs.getString("TABLE_NAME");
          
          if (! EXCLUDED_TABLES.contains(tableName)) {
            tableNames.add(tableRefs.getString("TABLE_NAME"));
          }
        }

        return tableNames;
      }
    });
  }
}