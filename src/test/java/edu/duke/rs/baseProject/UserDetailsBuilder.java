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
  public static final String PASSWORD = "abc123ABC";
  public static final String SECRET = "secret";
  
  public UserDetails build(final Long userId, final String email, final RoleName... roles) {
    final Set<Role> roleEntities = new HashSet<Role>(roles.length);
    
    for (int i = 0; i < roles.length; i++) {
      roleEntities.add(createRole(roles[i]));
    }
    
    final User user = createUser(userId, email, roleEntities);
    
    return new AppPrincipal(user, false, false);
  }
  
  protected User createUser(final Long userId, final String email, final Set<Role> roles) {
    final User user = new User();
    user.setFirstName("zzzz");
    user.setId(userId);
    user.setLastName("ssss");
    user.setPassword(createPassword(PASSWORD));
    user.setUsername("zzzzssss");
    user.setEmail(email);
    user.setSecret(createSecret());
    user.setTimeZone(TimeZone.getTimeZone("UTC"));
    user.setRoles(roles);
    
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
    
    result.add(createPrivilege(PrivilegeName.VIEW_USERS));
    
    if (roleName.equals(RoleName.ADMINISTRATOR)) {
      result.add(createPrivilege(PrivilegeName.EDIT_USERS));
    }
    
    return result;
  }
  
  protected Privilege createPrivilege(final PrivilegeName privilegeName) {
    return new Privilege(privilegeName);
  }
  
  protected String createPassword(final String password) {
    return password;
  }
  
  protected String createSecret() {
    return SECRET;
  }
}
