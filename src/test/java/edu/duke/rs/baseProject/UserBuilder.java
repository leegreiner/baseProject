package edu.duke.rs.baseProject;

import java.util.HashSet;
import java.util.Set;

import edu.duke.rs.baseProject.role.Privilege;
import edu.duke.rs.baseProject.role.PrivilegeName;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.user.User;

public class UserBuilder implements UsrBuilder { 
  @Override
  public User build(final String username, final String password, final String firstName,
      final String lastName, final String email, final Set<RoleName> roleNames) {
      final Set<Role> roles = new HashSet<Role>(roleNames == null ? 0 : roleNames.size());
    
      if (roleNames != null) {
        roleNames.forEach(roleName -> roles.add(createRole(roleName)));
      }
      
      final User user = new User(username, password, firstName, lastName, email, roles);
      user.setPassword(password);
      
      return user;
  }
  
  protected Role createRole(final RoleName roleName) {
    final Set<Privilege> privileges = this.createRolePrivileges(roleName);
    final Role role = new Role(roleName);
    
    role.setPrivileges(privileges);
    
    return role;
  }
  
  private Set<Privilege> createRolePrivileges(final RoleName roleName) {
    final Set<Privilege> result = new HashSet<Privilege>();
    
    result.add(new Privilege(PrivilegeName.VIEW_USERS));
    
    if (roleName.equals(RoleName.ADMINISTRATOR)) {
      result.add(new Privilege(PrivilegeName.EDIT_USERS));
    }
    
    return result;
  }
}
