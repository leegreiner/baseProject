package edu.duke.rs.baseProject.security;

import org.springframework.validation.annotation.Validated;

import edu.duke.rs.baseProject.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Validated
public interface LoginAttemptService {
  void loginFailed(@NotBlank String username);
  void loginSucceeded(@NotBlank String username);
  boolean isBlocked(@NotNull User user);
  boolean isClientIpBlocked();
}
