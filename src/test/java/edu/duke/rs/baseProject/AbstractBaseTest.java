package edu.duke.rs.baseProject;

import java.util.TimeZone;

import org.junit.jupiter.api.BeforeAll;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public abstract class AbstractBaseTest {
  protected static final ProjectionFactory PROJECTION_FACTORY = new SpelAwareProxyProjectionFactory();
  
  @BeforeAll
  public static void beforeAllTest() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
}
