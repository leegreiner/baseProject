package edu.duke.rs.baseProject;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class BaseProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseProjectApplication.class, args);
	}
}
