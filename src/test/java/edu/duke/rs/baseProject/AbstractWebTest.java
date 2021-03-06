package edu.duke.rs.baseProject;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.mrbean.MrBeanModule;

import edu.duke.rs.baseProject.error.ApplicationErrorController;

@ExtendWith(SpringExtension.class)
public abstract class AbstractWebTest extends AbstractBaseTest {
  protected static final String LOCAL_HOST = "http://localhost";
  protected static final String NOT_AUTORIZED_MAPPING = ApplicationErrorController.ERROR_MAPPING + "?error=403";
  public static final String API = "/api";
  protected static final ObjectMapper mapper;
  
  static {
    mapper = new ObjectMapper();
    mapper.registerModule(new MrBeanModule());
    mapper.registerModule(new JavaTimeModule());
  };
}
