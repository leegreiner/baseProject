package edu.duke.rs.baseProject;

import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public abstract class AbstractBaseTest {
  protected static final ProjectionFactory PROJECTION_FACTORY = new SpelAwareProxyProjectionFactory();
}
