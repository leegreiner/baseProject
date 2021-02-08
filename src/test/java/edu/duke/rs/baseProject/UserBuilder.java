package edu.duke.rs.baseProject;

import java.util.Set;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.user.User;

public class UserBuilder {
  private static final String SECRET = "secret";
  
  public User build(final String username, final String password, final String firstName,
      final String lastName, final String email, final Set<Role> roles) {
      final User user = new User(username, password, firstName, lastName, email, roles);
      user.setPassword(password);
      user.setSecret(SECRET);
      
      return user;
  }
}
