package edu.duke.rs.baseProject.security.password;

import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import edu.duke.rs.baseProject.user.User;

@Validated
public interface PasswordExpirationStrategy {
  
  boolean isPasswordExpired(@NotNull User user);

}
