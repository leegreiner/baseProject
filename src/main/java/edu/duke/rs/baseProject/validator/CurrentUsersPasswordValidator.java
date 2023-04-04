package edu.duke.rs.baseProject.validator;

import edu.duke.rs.baseProject.security.PersistentSecurityUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CurrentUsersPasswordValidator implements ConstraintValidator<CurrentUsersPassword, String> {
  private final PersistentSecurityUtils persistentSecurityUtils;
  
  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    return (password == null || persistentSecurityUtils.currentUserPasswordMatches(password));
  }
}
