package edu.duke.rs.baseProject;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;

import edu.duke.rs.baseProject.repository.ExtendedJpaRepositoryImpl;

@EnableJpaRepositories(basePackages = "edu.duke.rs.baseProject",
repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
public abstract class AbstractRepositoryTest extends AbstractBaseTest {
  @Autowired
  private EntityManager entityManager;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired(required = false)
  private CacheManager cacheManager;
  
  @BeforeEach
  public void resetState() throws Exception {
    entityManager.clear();
    TestUtils.resetState(jdbcTemplate, cacheManager);
  }
}
