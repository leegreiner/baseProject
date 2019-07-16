package edu.duke.rs.baseProject;

import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

@SpringBootApplication
public class BaseProjectApplication {

	public static void main(String[] args) {
		SpringApplication.run(BaseProjectApplication.class, args);
	}
	
	@Bean
	@Profile("local")
	public CommandLineRunner init(final UserRepository userRepository,
			final RoleRepository roleRepository, final PasswordEncoder passwordEncoder) {
		return (args) -> {
			final Set<Role> roles = new HashSet<Role>();
			roles.add(roleRepository.save(new Role(RoleName.USER)));
			userRepository.save(new User("username", passwordEncoder.encode("password"), "Default", "User", roles));
			userRepository.save(new User("billstafford", passwordEncoder.encode("password"), "Bill", "Stafford", roles));
			userRepository.save(new User("briancole", passwordEncoder.encode("password"), "Brian", "Cole", roles));
			userRepository.save(new User("samsmith", passwordEncoder.encode("password"), "Sam", "Smith", roles));
			userRepository.save(new User("perterpiper", passwordEncoder.encode("password"), "Peter", "Piper", roles));
			userRepository.save(new User("johnjameson", passwordEncoder.encode("password"), "John", "Jameson", roles));
			userRepository.save(new User("fredflannery", passwordEncoder.encode("password"), "Fred", "Flannery", roles));
			userRepository.save(new User("stevebosworth", passwordEncoder.encode("password"), "Steve", "Bosworth", roles));
			userRepository.save(new User("harrythompson", passwordEncoder.encode("password"), "Harry", "Thompson", roles));
			userRepository.save(new User("peterparker", passwordEncoder.encode("password"), "Peter", "Parker", roles));
			userRepository.save(new User("janejohnson", passwordEncoder.encode("password"), "Jane", "Johnson", roles));
			userRepository.save(new User("marysmith", passwordEncoder.encode("password"), "Mary", "Smith", roles));
			userRepository.save(new User("gregjohansen", passwordEncoder.encode("password"), "Greg", "Johansen", roles));
			
		};
	}
}
