package edu.duke.rs.baseProject.security.password;
import edu.duke.rs.baseProject.user.User;

public class PasswordNeverExpiresStrategy implements PasswordExpirationStrategy{
  
  @Override
  public boolean isPasswordExpired(User user) {
    return false;
  }

}
