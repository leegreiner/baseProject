package edu.duke.rs.baseProject.security.password;

import java.util.ArrayList;
import java.util.List;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.LengthRule;
import org.passay.PasswordData;
import org.passay.Rule;
import org.passay.WhitespaceRule;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.security.SecurityProperties;

@Component
public class PasswordValidatorImpl implements PasswordValidator {
  private transient final org.passay.PasswordValidator passwordValidator;
  private transient final PasswordEncoder passwordEncoder;

  public PasswordValidatorImpl(final SecurityProperties securityProperties,
      final PasswordEncoder passwordEncoder) {
    final List<Rule> rules = new ArrayList<Rule>();
    
    if(securityProperties.getPassword().getNumberLowerCase() > 0) {
      rules.add(new CharacterRule(EnglishCharacterData.LowerCase, securityProperties.getPassword().getNumberLowerCase()));
    }
    
    if(securityProperties.getPassword().getNumberUpperCase() > 0) {
      rules.add(new CharacterRule(EnglishCharacterData.UpperCase, securityProperties.getPassword().getNumberUpperCase()));
    }
    
    if(securityProperties.getPassword().getNumberDigits() > 0) {
      rules.add(new CharacterRule(EnglishCharacterData.Digit, securityProperties.getPassword().getNumberDigits()));
    }
    
    if(securityProperties.getPassword().getNumberSpecial() > 0) {
      rules.add(new CharacterRule(new SpecialCharacterData(), securityProperties.getPassword().getNumberSpecial()));
    }
    
    rules.add(new WhitespaceRule());
    rules.add(new LengthRule(securityProperties.getPassword().getMinLength(), securityProperties.getPassword().getMaxLength()));
    
    this.passwordEncoder = passwordEncoder;
    this.passwordValidator = new org.passay.PasswordValidator(rules);
  }

  @Override
  public boolean isValid(String password) {
    return passwordValidator.validate(new PasswordData(password)).isValid();
  }

  @Override
  public boolean isSame(String password, String encryptedPassword) {
    return passwordEncoder.matches(password, encryptedPassword);
  }
}
