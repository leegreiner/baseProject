package edu.duke.rs.baseProject;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest(properties = {"spring.cloud.config.enabled=false"})
public class BaseProjectApplicationTests extends AbstractBaseTest {
	@Test
	public void contextLoads() {
	}
}
