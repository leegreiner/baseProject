package edu.duke.rs.baseProject;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.springframework.security.core.userdetails.UserDetails;

import edu.duke.rs.baseProject.role.Privilege;
import edu.duke.rs.baseProject.role.PrivilegeName;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.user.User;

public class UserDetailsBuilder {
  private UserDetailsBuilder() {
  }
  
  public static UserDetails build(final Long userId, final RoleName... roles) {
    final Set<Role> roleEntities = new HashSet<Role>(roles.length);
    
    for (int i = 0; i < roles.length; i++) {
      final Role role = new Role(roles[i]);
      role.setId(Long.valueOf(i + 1));
      role.setPrivileges(getRolePrivileges(roles[i]));
      roleEntities.add(role);
    }
    
    final User user = new User();
    user.setFirstName("John");
    user.setId(userId);
    user.setLastName("Smith");
    user.setUsername("johnsmith");
    user.setEmail("johnSmith@gmail.com");
    user.setRoles(roleEntities);
    user.setTimeZone(TimeZone.getTimeZone("UTC"));
    
    return new AppPrincipal(user, false, false);
  }
  
  private static Set<Privilege> getRolePrivileges(final RoleName roleName) {
    final Set<Privilege> result = new HashSet<Privilege>();
    
    result.add(new Privilege(PrivilegeName.VIEW_USERS));
    
    if (roleName.equals(RoleName.ADMINISTRATOR)) {
      result.add(new Privilege(PrivilegeName.EDIT_USERS));
    }
    
    return result;
  }
}
