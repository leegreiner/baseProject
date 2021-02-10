package edu.duke.rs.baseProject;

import java.util.Set;

import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.user.User;

public interface UsrBuilder {

  User build(String username, String password, String firstName, String lastName, String email,
      Set<RoleName> roleNames);

}