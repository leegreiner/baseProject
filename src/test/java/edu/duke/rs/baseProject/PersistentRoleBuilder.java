package edu.duke.rs.baseProject;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.role.Privilege;
import edu.duke.rs.baseProject.role.PrivilegeName;
import edu.duke.rs.baseProject.role.PrivilegeRepository;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PersistentRoleBuilder {
  private final PrivilegeRepository privilegeRepository;
  private final RoleRepository roleRepository;
  
  public void buildAll() {
    final Map<PrivilegeName, Privilege> privileges = new HashMap<PrivilegeName, Privilege>();
    
    for (final PrivilegeName privilegeName : PrivilegeName.values()) {
      privileges.put(privilegeName, createPrivilege(privilegeName));
    }
    
    for (final RoleName roleName : RoleName.values()) {
      final Role role = new Role(roleName);
      role.setPrivileges(new HashSet<Privilege>());
      
      role.getPrivileges().add(privileges.get(PrivilegeName.VIEW_USERS));
      
      if (roleName.equals(RoleName.ADMINISTRATOR)) {
        role.getPrivileges().add(privileges.get(PrivilegeName.EDIT_USERS));
      }
      
      this.roleRepository.save(role);
    }
  }
  
  private Privilege createPrivilege(final PrivilegeName privilegeName) {
    return this.privilegeRepository.save(new Privilege(privilegeName));
  }
}
