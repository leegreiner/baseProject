package edu.duke.rs.baseProject;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PersistentUserBuilder implements UsrBuilder {
  private final PasswordEncoder passwordEncoder;
  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  
  public User build(final String username, final String password, final String firstName,
      final String lastName, final String email, final Set<RoleName> roleNames) {

    final Set<Role> roles = new HashSet<Role>();
    
    for (final RoleName roleName : roleNames) {
      roles.add(this.roleRepository.getByName(roleName)
          .orElseThrow(() -> new IllegalArgumentException("Role " + roleName + " not foud")));
    }
    
    final User user = new User(username, password, firstName, lastName, email, roles);
    user.setPassword(passwordEncoder.encode(password));
    
    return userRepository.save(user);
  }
}
