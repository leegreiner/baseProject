package edu.duke.rs.baseProject;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
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
  public void resetState() throws Exception {
    TestUtils.resetState(jdbcTemplate, cacheManager);
  }
}
