package edu.duke.rs.baseProject.user.passwordReset;

import javax.validation.Valid;

import edu.duke.rs.baseProject.user.User;

public class DoNothingPasswordResetService implements PasswordResetService {
  @Override
  public void initiatePasswordReset(@Valid final InitiatePasswordResetDto passwordResetDto) {}
  
  @Override
  public void initiatePasswordReset(final User user) {}


  @Override
  public void processPasswordReset(@Valid final PasswordResetDto passwordResetDto) {}

  @Override
  public void expirePasswordResetIds() {}

}
