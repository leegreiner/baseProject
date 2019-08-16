package edu.duke.rs.baseProject;

import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

@RunWith(SpringRunner.class)
public abstract class AbstractWebTest extends AbstractBaseTest {
  protected static final String LOCAL_HOST = "http://localhost";
  public static final String API = "/api";
  protected static final ObjectMapper mapper;
  
  static {
    mapper = new ObjectMapper();
    mapper.registerModule(new MrBeanModule());
  };
}
