package edu.duke.rs.baseProject;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import dev.samstevens.totp.secret.SecretGenerator;
import edu.duke.rs.baseProject.role.Privilege;
import edu.duke.rs.baseProject.role.PrivilegeName;
import edu.duke.rs.baseProject.role.PrivilegeRepository;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PersistentUserDetailsBuilder extends UserDetailsBuilder {
  private final UserRepository userRepository;
  private final PrivilegeRepository privilegeRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;
  private final SecretGenerator secretGenerator;
  
  public UserDetails build(final String email, final RoleName... roles) {
    return super.build(null, email, roles);
  }
  
  @Override
  public UserDetails build(final Long id, final String email, final RoleName... roles) {
    throw new IllegalArgumentException("Don't specify id when using persistent entities");
  }
  
  @Override
  protected User createUser(final Long userId, final String email, final Set<Role> roles) {
    return this.userRepository.save(super.createUser(null, email, roles));
  }
  
  @Override
  protected Role createRole(final RoleName roleName) {
    return this.roleRepository.getByName(roleName)
        .orElseThrow(() -> new IllegalArgumentException("Role " + roleName + " not found"));
  }
  
  @Override
  protected Privilege createPrivilege(final PrivilegeName privilegeName) {
    return this.privilegeRepository.findByName(privilegeName)
        .orElseThrow(() -> new IllegalArgumentException("Privilege " + privilegeName + " not found"));
  }
  
  @Override
  protected String createPassword(final String password) {
    return this.passwordEncoder.encode(password);
  }
  
  @Override
  protected String createSecret() {
    return this.secretGenerator.generate();
  }
}
