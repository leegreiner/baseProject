package edu.duke.rs.baseProject;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import edu.duke.rs.baseProject.repository.ExtendedJpaRepositoryImpl;

@EnableJpaRepositories(basePackages = "edu.duke.rs.baseProject",
  repositoryBaseClass = ExtendedJpaRepositoryImpl.class)
@Import(TestUtilsService.class)
@DataJpaTest
@EnableJpaAuditing
public abstract class AbstractRepositoryTest extends AbstractBaseTest {
  @Autowired
  protected TestEntityManager testEntityManager;
  @Autowired
  private TestUtilsService testUtilsService;
  
  @BeforeEach
  public void resetState() throws Exception {
    this.testUtilsService.resetState();
  }
}
