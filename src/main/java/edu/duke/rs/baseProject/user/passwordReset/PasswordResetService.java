package edu.duke.rs.baseProject.user.passwordReset;

import org.springframework.validation.annotation.Validated;

import edu.duke.rs.baseProject.user.User;
import jakarta.validation.Valid;

@Validated
public interface PasswordResetService {
  void initiatePasswordReset(@Valid InitiatePasswordResetDto passwordResetDto);
  void initiatePasswordReset(User user);
  void processPasswordReset(@Valid PasswordResetDto passwordResetDto);
  void expirePasswordResetIds();
}
