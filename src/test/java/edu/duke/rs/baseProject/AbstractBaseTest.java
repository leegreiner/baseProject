package edu.duke.rs.baseProject;

import java.util.Random;
import java.util.TimeZone;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
public abstract class AbstractBaseTest {
  protected static final ProjectionFactory PROJECTION_FACTORY = new SpelAwareProxyProjectionFactory();
  protected EasyRandom easyRandom;
  
  @BeforeAll
  public static void beforeAllTest() {
    TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
  }
  
  @BeforeEach
  public void beforeEach() {
    easyRandom = new EasyRandom(new EasyRandomParameters().seed(new Random().nextLong()).stringLengthRange(10, 15));
  }
}
