package edu.duke.rs.baseProject.security.password;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.duke.rs.baseProject.security.SecurityProperties;

public class PasswordValidatorUnitTest {
  private static final org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder ENCODER = 
      new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
  private PasswordEncoder NO_OP_PASSWORD_ENCODER = new NoOpPasswordEncoder();
  
  @Test
  public void whenWhiteSpaceInPassword_thenValidationFails() {
    final char[] whiteSpaceChars = {(byte) 0x09, (byte) 0x0A, (byte) 0x0B, (byte) 0x0C, (byte) 0x0D, (byte) 0x20};
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberLowerCase(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    
    for (final char whiteSpace : whiteSpaceChars) {
      final String pwd = "abc " + whiteSpace + "defg";
      assertThat(validator.isValid(pwd), equalTo(false));
    }
  }
  
  @Test
  public void whenFewerCharactersEntered_thenValidationFails() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberLowerCase(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    final String pwd = "ab";
    
    assertThat(validator.isValid(pwd), equalTo(false));
  }
  
  @Test
  public void whenMoreCharactersEntered_thenValidationFails() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberLowerCase(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    
    final String pwd = "abcdefghi";
    
    assertThat(validator.isValid(pwd), equalTo(false));
  }
  
  @Test
  public void whenCorrectlyEnteredUpperCase_thenValidationSucceeds() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberUpperCase(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    
    final String pwd = "ABCDEFGH";
    
    assertThat(validator.isValid(pwd), equalTo(true));
  }
  
  @Test
  public void whenCorrectlyEnteredDigit_thenValidationSucceeds() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    final String pwd = "12345678";
    
    assertThat(validator.isValid(pwd), equalTo(true));
  }
  
  @Test
  public void whenCorrectlyEnteredSpecialChar_thenValidationSucceeds() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberSpecial(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    final String pwd = "@!#$%^&*";
    
    assertThat(validator.isValid(pwd), equalTo(true));
  }
  
  @Test
  public void whenFewerLowerCase_thenValidationFails() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(1);
    password.setNumberLowerCase(2);
    password.setNumberSpecial(1);
    password.setNumberUpperCase(1);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    final String pwd = "J8*a";
    assertThat(validator.isValid(pwd), equalTo(false));
  }
  
  @Test
  public void whenFewerUpperCase_thenValidationFails() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(1);
    password.setNumberLowerCase(1);
    password.setNumberSpecial(1);
    password.setNumberUpperCase(2);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    final String pwd = "J8*f";
    
    assertThat(validator.isValid(pwd), equalTo(false));
  }
  
  @Test
  public void whenFewerDigits_thenValidationFails() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(2);
    password.setNumberLowerCase(1);
    password.setNumberSpecial(1);
    password.setNumberUpperCase(1);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    final String pwd = "J8*a";
    
    assertThat(validator.isValid(pwd), equalTo(false));
  }
  
  @Test
  public void whenFewerSpecial_thenValidationFails() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(1);
    password.setNumberLowerCase(1);
    password.setNumberSpecial(2);
    password.setNumberUpperCase(1);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    final String pwd = "J8*a";
    
    assertThat(validator.isValid(pwd), equalTo(false));
  }
  
  @Test
  public void whenCorrectlyEntered_thenValidationSucceeds() {
    final List<String> passwords = List.of(
        "abc123D@",
        "@#$KU2*d",
        "J8*a",
        "&KJ*&a3"
    );
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(1);
    password.setNumberLowerCase(1);
    password.setNumberSpecial(1);
    password.setNumberUpperCase(1);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, NO_OP_PASSWORD_ENCODER);
    
    for (final String pwd : passwords) {
      assertThat(validator.isValid(pwd), equalTo(true));
    } 
  }
  
  @Test
  public void whenEncodedPasswordDiffernt_theIsSameIsFalse() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, ENCODER);
    
    assertThat(validator.isSame("abc1", ENCODER.encode("abc12")), equalTo(false));
  }
  
  @Test
  public void whenEncodedPasswordSame_theIsSameIsTrue() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(3);
    password.setMaxLength(8);
    password.setMinLength(3);
    
    final PasswordValidator validator = 
        new PasswordValidatorImpl(securityProperties, ENCODER);
    
    assertThat(validator.isSame("abc12", ENCODER.encode("abc12")), equalTo(true));
  }
}
