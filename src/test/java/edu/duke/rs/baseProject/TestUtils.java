package edu.duke.rs.baseProject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.cache.CacheManager;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCallback;

public class TestUtils {

  private TestUtils() {}
  
  public static void resetState(final JdbcTemplate jdbcTemplate, final CacheManager cacheManager) throws Exception {
    cleanAllDatabases(jdbcTemplate);
    cleanAllCaches(cacheManager);
  }
  
  private static void cleanAllDatabases(final JdbcTemplate jdbcTemplate) throws Exception {
    for (String tableName : List.of("users_to_roles", "users", "role")) {
      jdbcTemplate.execute("DELETE FROM " + tableName, new PreparedStatementCallback<Integer>() {
        @Override
        public Integer doInPreparedStatement(PreparedStatement ps) throws SQLException, DataAccessException {
          final Connection connection = ps.getConnection();
          boolean currentAutoCommit = connection.getAutoCommit();
          
          if (! currentAutoCommit) {
            connection.setAutoCommit(! currentAutoCommit);
          }
          
          ps.execute();
          
          connection.commit();
          
          connection.setAutoCommit(currentAutoCommit);
          return null;
        }
      });
    }
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
