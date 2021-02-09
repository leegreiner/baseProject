package edu.duke.rs.baseProject;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.samstevens.totp.secret.SecretGenerator;
import edu.duke.rs.baseProject.annotations.EnableSAMLSSOWhenProfileActive;
import edu.duke.rs.baseProject.role.Privilege;
import edu.duke.rs.baseProject.role.PrivilegeName;
import edu.duke.rs.baseProject.role.PrivilegeRepository;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

@SpringBootApplication
@ServletComponentScan
@EnableSAMLSSOWhenProfileActive("samlSecurity")
public class BaseProjectApplication {

	public static void main(String[] args) {
	  TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
	  final SpringApplication springApplication = new SpringApplication(BaseProjectApplication.class);
	  // -XX:StartFlightRecording:filename=recording.jfr,duration=10s
	  //springApplication.setApplicationStartup(new FlightRecorderApplicationStartup());
		springApplication.run(args);
	}
	
	@Bean
  @Profile({"local", "docker"})
  public CommandLineRunner init(final UserRepository userRepository,
      final RoleRepository roleRepository, final PrivilegeRepository privilegeRepository,
      final PasswordEncoder passwordEncoder, final SecretGenerator secretGenerator) {
    return (args) -> {
      final Privilege viewUsersPrivilege = privilegeRepository.save(new Privilege(PrivilegeName.VIEW_USERS));
      final Set<Privilege> userPrivileges = new HashSet<Privilege>();
      userPrivileges.add(viewUsersPrivilege);
      final Set<Privilege> administratorPrivileges = new HashSet<Privilege>();
      administratorPrivileges.add(viewUsersPrivilege);
      administratorPrivileges.add(privilegeRepository.save(new Privilege(PrivilegeName.EDIT_USERS)));
      Role userRole = new Role(RoleName.USER);
      userRole.setPrivileges(userPrivileges);
      userRole = roleRepository.save(userRole);
      Role administratorRole = new Role(RoleName.ADMINISTRATOR);
      administratorRole.setPrivileges(administratorPrivileges);
      administratorRole = roleRepository.save(administratorRole);
      final Set<Role> userRoles = Set.of(userRole);
      final Set<Role> administratorRoles = Set.of(administratorRole);

      userRepository.save(createUser("username", passwordEncoder.encode("password"), secretGenerator.generate(), "Default", "User","defaultUser@gmail.com", userRoles));
      userRepository.save(createUser("billstafford", passwordEncoder.encode("password"), secretGenerator.generate(), "Bill", "Stafford","billStafford@gmail.com", userRoles));
      userRepository.save(createUser("briancole", passwordEncoder.encode("password"), secretGenerator.generate(), "Brian", "Cole","brianCole@gmail.com", userRoles));
      userRepository.save(createUser("samsmith", passwordEncoder.encode("password"), secretGenerator.generate(), "Sam", "Smith","samSmith@gmail.com", userRoles));
      userRepository.save(createUser("perterpiper", passwordEncoder.encode("password"), secretGenerator.generate(), "Peter", "Piper","peterPiper@gmail.com", userRoles));
      userRepository.save(createUser("johnjameson", passwordEncoder.encode("password"), secretGenerator.generate(), "John", "Jameson","johnJameson@gmail.com", userRoles));
      userRepository.save(createUser("fredflannery", passwordEncoder.encode("password"), secretGenerator.generate(), "Fred", "Flannery","fredFlannery@gmail.com", userRoles));
      userRepository.save(createUser("stevebosworth", passwordEncoder.encode("password"), secretGenerator.generate(), "Steve", "Bosworth","steveBosworth@gmail.com", userRoles));
      userRepository.save(createUser("harrythompson", passwordEncoder.encode("password"), secretGenerator.generate(), "Harry", "Thompson","harryThompson@gmail.com", userRoles));
      userRepository.save(createUser("peterparker", passwordEncoder.encode("password"), secretGenerator.generate(), "Peter", "Parker","peterParker@gmail.com", userRoles));
      userRepository.save(createUser("janejohnson", passwordEncoder.encode("password"), secretGenerator.generate(), "Jane", "Johnson","janeJohnson@gmail.com", userRoles));
      userRepository.save(createUser("marysmith", passwordEncoder.encode("password"), secretGenerator.generate(), "Mary", "Smith","marySmith@gmail.com", userRoles));
      userRepository.save(createUser("grein003", passwordEncoder.encode("password"), secretGenerator.generate(), "Lee", "Greiner","lee.greiner@duke.edu", administratorRoles));
    };
  }
	
	private static User createUser(final String userName, final String password, final String secret,
	    final String firstName, final String lastName, final String email, final Set<Role> roles) {
    final User user = new User(userName, password, firstName, lastName, email, roles);
    user.setLastPasswordChange(LocalDateTime.now());
    user.setSecret(secret);
    
    return user;
  }
}
