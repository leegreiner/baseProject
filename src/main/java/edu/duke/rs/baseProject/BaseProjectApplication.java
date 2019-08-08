package edu.duke.rs.baseProject;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import javax.annotation.PostConstruct;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.duke.rs.baseProject.annotations.EnableSAMLSSOWhenProfileActive;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

@SpringBootApplication
@EnableSAMLSSOWhenProfileActive("samlSecurity")
public class BaseProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseProjectApplication.class, args);
		
	}
	
	@PostConstruct
	public void started() {
	  TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	}
	
	@Bean
	@Profile("local")
	public CommandLineRunner init(final UserRepository userRepository,
			final RoleRepository roleRepository, final PasswordEncoder passwordEncoder) {
		return (args) -> {
			final Set<Role> roles = new HashSet<Role>();
			roles.add(roleRepository.save(new Role(RoleName.USER)));
			userRepository.save(new User("username", passwordEncoder.encode("password"), "Default", "User","defaultUser@gmail.com", roles));
			userRepository.save(new User("billstafford", passwordEncoder.encode("password"), "Bill", "Stafford","billStafford@gmail.com", roles));
			userRepository.save(new User("briancole", passwordEncoder.encode("password"), "Brian", "Cole","brianCole@gmail.com", roles));
			userRepository.save(new User("samsmith", passwordEncoder.encode("password"), "Sam", "Smith","samSmith@gmail.com", roles));
			userRepository.save(new User("perterpiper", passwordEncoder.encode("password"), "Peter", "Piper","peterPiper@gmail.com", roles));
			userRepository.save(new User("johnjameson", passwordEncoder.encode("password"), "John", "Jameson","johnJameson@gmail.com", roles));
			userRepository.save(new User("fredflannery", passwordEncoder.encode("password"), "Fred", "Flannery","fredFlannery@gmail.com", roles));
			userRepository.save(new User("stevebosworth", passwordEncoder.encode("password"), "Steve", "Bosworth","steveBosworth@gmail.com", roles));
			userRepository.save(new User("harrythompson", passwordEncoder.encode("password"), "Harry", "Thompson","harryThompson@gmail.com", roles));
			userRepository.save(new User("peterparker", passwordEncoder.encode("password"), "Peter", "Parker","peterParker@gmail.com", roles));
			userRepository.save(new User("janejohnson", passwordEncoder.encode("password"), "Jane", "Johnson","janeJohnson@gmail.com", roles));
			userRepository.save(new User("marysmith", passwordEncoder.encode("password"), "Mary", "Smith","marySmith@gmail.com", roles));
			userRepository.save(new User("grein003", passwordEncoder.encode("password"), "Lee", "Greiner","lee.greiner@duke.edu", roles));
			
		};
	}
}
