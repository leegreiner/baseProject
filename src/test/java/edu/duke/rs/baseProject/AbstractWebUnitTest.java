package edu.duke.rs.baseProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import edu.duke.rs.baseProject.config.GoogleProperties;

public abstract class AbstractWebUnitTest extends AbstractWebTest {
  @Autowired
  protected MockMvc mockMvc;
  @MockBean
  protected GoogleProperties googleProperties;

}
