package edu.duke.rs.baseProject.security.password;

import java.util.ArrayList;
import java.util.List;

import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.security.SecurityProperties;

@Component
public class PasswordGeneratorImpl implements PasswordGenerator {
  private transient final SecurityProperties securityProperties;
  private transient final PasswordEncoder passwordEncoder;
  private transient final org.passay.PasswordGenerator passwordGenerator;
  private transient final List<CharacterRule> rules;
  
  public PasswordGeneratorImpl(final SecurityProperties securityProperties,
      final PasswordEncoder passwordEncoder) {
    this.securityProperties = securityProperties;
    this.passwordEncoder = passwordEncoder;
    this.passwordGenerator = new org.passay.PasswordGenerator();
    rules = new ArrayList<CharacterRule>();
    
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
  }

  @Override
  public String generate() {
    return passwordEncoder.encode(passwordGenerator.generatePassword(securityProperties.getPassword().getMaxLength(), rules));
  }
}
