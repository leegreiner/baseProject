package edu.duke.rs.baseProject;

import java.time.LocalDateTime;
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
			userRepository.save(createUser("username", passwordEncoder.encode("password"), "Default", "User","defaultUser@gmail.com", roles));
			userRepository.save(createUser("billstafford", passwordEncoder.encode("password"), "Bill", "Stafford","billStafford@gmail.com", roles));
			userRepository.save(createUser("briancole", passwordEncoder.encode("password"), "Brian", "Cole","brianCole@gmail.com", roles));
			userRepository.save(createUser("samsmith", passwordEncoder.encode("password"), "Sam", "Smith","samSmith@gmail.com", roles));
			userRepository.save(createUser("perterpiper", passwordEncoder.encode("password"), "Peter", "Piper","peterPiper@gmail.com", roles));
			userRepository.save(createUser("johnjameson", passwordEncoder.encode("password"), "John", "Jameson","johnJameson@gmail.com", roles));
			userRepository.save(createUser("fredflannery", passwordEncoder.encode("password"), "Fred", "Flannery","fredFlannery@gmail.com", roles));
			userRepository.save(createUser("stevebosworth", passwordEncoder.encode("password"), "Steve", "Bosworth","steveBosworth@gmail.com", roles));
			userRepository.save(createUser("harrythompson", passwordEncoder.encode("password"), "Harry", "Thompson","harryThompson@gmail.com", roles));
			userRepository.save(createUser("peterparker", passwordEncoder.encode("password"), "Peter", "Parker","peterParker@gmail.com", roles));
			userRepository.save(createUser("janejohnson", passwordEncoder.encode("password"), "Jane", "Johnson","janeJohnson@gmail.com", roles));
			userRepository.save(createUser("marysmith", passwordEncoder.encode("password"), "Mary", "Smith","marySmith@gmail.com", roles));
			userRepository.save(createUser("grein003", passwordEncoder.encode("password"), "Lee", "Greiner","lee.greiner@duke.edu", roles));
			
		};
	}
	
	private static User createUser(final String userName, final String password, final String firstName, final String lastName, final String email,
	    final Set<Role> roles) {
	  final User user = new User(userName, password, firstName, lastName, email, roles);
	  user.setLastPasswordChange(LocalDateTime.now());
	  
	  return user;
	}
}
