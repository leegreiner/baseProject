package edu.duke.rs.baseProject.security.password;

import org.springframework.validation.annotation.Validated;

import edu.duke.rs.baseProject.user.User;
import jakarta.validation.constraints.NotNull;

@Validated
public interface PasswordExpirationStrategy {
  boolean isPasswordExpired(@NotNull User user);
}
