package edu.duke.rs.baseProject;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

@SpringBootApplication
@ServletComponentScan
@OpenAPIDefinition(info = @Info(title = "Baseapp REST API", description = "Rest API for Baseapp"))
public class BaseProjectApplication {

	public static void main(String[] args) {
	  TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	  final SpringApplication springApplication = new SpringApplication(BaseProjectApplication.class);
	  // -XX:StartFlightRecording:filename=recording.jfr,duration=10s
	  //springApplication.setApplicationStartup(new FlightRecorderApplicationStartup());
		springApplication.run(args);
	}
}
