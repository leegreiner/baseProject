package edu.duke.rs.baseProject;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class BaseProjectApplication {

	public static void main(String[] args) {
	  TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	  final SpringApplication springApplication = new SpringApplication(BaseProjectApplication.class);
	  // -XX:StartFlightRecording:filename=recording.jfr,duration=10s
	  //springApplication.setApplicationStartup(new FlightRecorderApplicationStartup());
		springApplication.run(args);
	}
}
