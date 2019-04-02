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
			userRepository.save(new User("username", passwordEncoder.encode("password"), roles));
		};
	}
}
