package edu.duke.rs.baseProject.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import edu.duke.rs.baseProject.security.PersistentSecurityUtils;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class CurrentUsersPasswordValidator implements ConstraintValidator<CurrentUsersPassword, String> {
  private final PersistentSecurityUtils persistentSecurityUtils;
  
  @Override
  public boolean isValid(String password, ConstraintValidatorContext context) {
    return (password == null || persistentSecurityUtils.currentUserPasswordMatches(password));
  }
}
