package edu.duke.rs.baseProject;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.jdbc.JdbcTestUtils;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractWebIntegrationTest extends AbstractWebTest {
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired(required = false)
  private CacheManager cacheManager;
  
  @Before
  public void resetState() {
    cleanAllDatabases();
    cleanAllCaches();
  }
  
  private void cleanAllDatabases() {
    JdbcTestUtils.deleteFromTables(jdbcTemplate, "users_to_roles", "users", "role");
  }
  
  private void cleanAllCaches() {
    if (cacheManager != null) {
      cacheManager.getCacheNames()
        .stream()
        .map(it -> cacheManager.getCache(it))
        .filter(it -> it != null)
        .forEach(it -> it.clear());
    }
  }
}
