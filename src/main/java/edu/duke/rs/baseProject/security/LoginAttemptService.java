package edu.duke.rs.baseProject.security;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import edu.duke.rs.baseProject.user.User;

@Validated
public interface LoginAttemptService {
  void loginFailed(@NotBlank String username);
  void loginSucceeded(@NotBlank String username);
  boolean isBlocked(@NotNull User user);
  boolean isClientIpBlocked();
}
