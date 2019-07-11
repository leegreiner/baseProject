package edu.duke.rs.baseProject;

import org.springframework.boot.test.mock.mockito.MockBean;

import edu.duke.rs.baseProject.config.GoogleProperties;

public class BaseWebMvcTest extends BaseWebTest {
  @MockBean
  protected GoogleProperties googleProperties;
}
