package edu.duke.rs.baseProject;

import java.util.HashSet;
import java.util.Set;

import org.springframework.security.core.userdetails.UserDetails;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.user.User;

public class UserDetailsBuilder {
  private UserDetailsBuilder() {
  }
  
  public static UserDetails build(RoleName... roles) {
    final Set<Role> roleEntities = new HashSet<Role>(roles.length);
    
    for (int i = 0; i < roles.length; i++) {
      final Role role = new Role(roles[i]);
      role.setId(Long.valueOf(i + 1));
      roleEntities.add(role);
    }
    
    final User user = new User();
    user.setFirstName("John");
    user.setId(Long.valueOf(1));
    user.setLastName("Smith");
    user.setUserName("johnsmith");
    user.setEmail("johnSmith@gmail.com");
    user.setRoles(roleEntities);
    
    return new AppPrincipal(user, false);
  }
}
