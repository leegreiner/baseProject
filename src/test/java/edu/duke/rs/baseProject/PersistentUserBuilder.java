package edu.duke.rs.baseProject;

import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dev.samstevens.totp.secret.SecretGenerator;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PersistentUserBuilder {
  private final PasswordEncoder passwordEncoder;
  private final SecretGenerator secretGenerator;
  private final UserRepository userRepository;
  private final UserBuilder userBuilder = new UserBuilder();
  
  public User build(final String username, final String password, final String firstName,
      final String lastName, final String email, final Set<Role> roles) {
    final User user = userBuilder.build(username, password, firstName, lastName, email, roles);
    user.setPassword(passwordEncoder.encode(password));
    user.setSecret(secretGenerator.generate());
    
    return userRepository.save(user);
  }
}
