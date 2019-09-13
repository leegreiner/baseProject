package edu.duke.rs.baseProject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
@EnableAutoConfiguration(exclude=CacheAutoConfiguration.class)
public class BaseProjectApplicationTests extends AbstractBaseTest {

	@Test
	public void contextLoads() {
	}

}
