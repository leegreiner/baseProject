package edu.duke.rs.baseProject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import edu.duke.rs.baseProject.config.GoogleProperties;
import edu.duke.rs.baseProject.config.WebSecurityConfig;

@Import(WebSecurityConfig.class)
public abstract class AbstractWebUnitTest extends AbstractWebTest {
  @Autowired
  protected MockMvc mockMvc;
  @MockBean(name = "googleProperties")
  protected GoogleProperties googleProperties;

}
