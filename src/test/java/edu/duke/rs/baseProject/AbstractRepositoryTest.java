package edu.duke.rs.baseProject;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;

public abstract class AbstractRepositoryTest extends AbstractBaseTest {
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired(required = false)
  private CacheManager cacheManager;
  
  @Before
  public void resetState() throws Exception {
    TestUtils.resetState(jdbcTemplate, cacheManager);
  }
}
