package edu.duke.rs.baseProject;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.user.User;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PersistentUserDetailsBuilder {
  public static final String PASSWORD = "passworD1";
  private static final String FIRST_NAME = "firstName";
  private static final String LAST_NAME = "lastName";
  private final PersistentUserBuilder persistentUserBuilder;
  
  public UserDetails build(final String userName, final String email, final Set<RoleName> roleNames) {
    final User user = this.persistentUserBuilder.build(userName, PASSWORD, FIRST_NAME, LAST_NAME, email, roleNames);
    
    return new AppPrincipal(user, false, false);
  }
}
