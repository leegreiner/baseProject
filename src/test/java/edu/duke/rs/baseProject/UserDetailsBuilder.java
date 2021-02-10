package edu.duke.rs.baseProject;

import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.user.User;

public class UserDetailsBuilder {
  private final UsrBuilder userBuilder = new UserBuilder();
  
  public UserDetails build(final Long userId, final String email, final Set<RoleName> roleNames) {
    final User user = this.userBuilder.build(email, email, email, email, email, roleNames);
    user.setId(userId);
    
    return new AppPrincipal(user, false, false);
  }
}
