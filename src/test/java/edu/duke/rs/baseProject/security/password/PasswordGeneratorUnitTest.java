package edu.duke.rs.baseProject.security.password;

import static com.jcabi.matchers.RegexMatchers.matchesPattern;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.regex.Pattern;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.duke.rs.baseProject.security.SecurityProperties;

public class PasswordGeneratorUnitTest {
  @Mock
  private PasswordEncoder passwordEncoder;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    when(passwordEncoder.encode(org.mockito.ArgumentMatchers.any(CharSequence.class))).thenAnswer(new Answer<String>() {
      @Override
      public String answer(InvocationOnMock invocation) throws Throwable {
        Object[] args = invocation.getArguments();
        return (String) args[0];
      }
    });
  }
  
  @Test
  public void whenSecurityPropertiesIndicateOnlyLowerCase_thenPasswordWithLowerCaseIsGenerated() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final int numLowerCase = 3;
    final int maxLength = 8;
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberLowerCase(numLowerCase);
    password.setMaxLength(maxLength);
    password.setMinLength(3);
    
    final PasswordGenerator generator = 
        new PasswordGeneratorImpl(securityProperties, passwordEncoder);
    
    final String result = generator.generate();
    
    assertThat(result.length(), equalTo(maxLength));
    assertThat(result, matchesPattern("^[a-z]+$"));
    verify(passwordEncoder, times(1)).encode(org.mockito.ArgumentMatchers.any(CharSequence.class));
    verifyNoMoreInteractions(passwordEncoder);
  }
  
  @Test
  public void whenSecurityPropertiesIndicateOnlyUpperCase_thenPasswordWithUpperCaseIsGenerated() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final int numUpperCase = 3;
    final int maxLength = 15;
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberUpperCase(numUpperCase);
    password.setMaxLength(maxLength);
    password.setMinLength(3);
    
    final PasswordGenerator generator = 
        new PasswordGeneratorImpl(securityProperties, passwordEncoder);
    
    final String result = generator.generate();
    
    assertThat(result.length(), equalTo(maxLength));
    assertThat(result, matchesPattern("^[A-Z]+$"));
    verify(passwordEncoder, times(1)).encode(org.mockito.ArgumentMatchers.any(CharSequence.class));
    verifyNoMoreInteractions(passwordEncoder);
  }
  
  @Test
  public void whenSecurityPropertiesIndicateOnlyDigits_thenPasswordWithDigitsIsGenerated() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final int numDigits = 3;
    final int maxLength = 15;
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberDigits(numDigits);
    password.setMaxLength(maxLength);
    password.setMinLength(3);
    
    final PasswordGenerator generator = 
        new PasswordGeneratorImpl(securityProperties, passwordEncoder);
    
    final String result = generator.generate();
    
    assertThat(result.length(), equalTo(maxLength));
    assertThat(result, matchesPattern("^\\d+$"));
    verify(passwordEncoder, times(1)).encode(org.mockito.ArgumentMatchers.any(CharSequence.class));
    verifyNoMoreInteractions(passwordEncoder);
  }
  
  @Test
  public void whenSecurityPropertiesIndicateOnlySpecial_thenPasswordWithSpecialIsGenerated() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final int numSpecial = 3;
    final int maxLength = 15;
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberSpecial(numSpecial);
    password.setMaxLength(maxLength);
    password.setMinLength(3);
    
    final PasswordGenerator generator = 
        new PasswordGeneratorImpl(securityProperties, passwordEncoder);
    
    final String result = generator.generate();
    
    assertThat(result.length(), equalTo(maxLength));
    assertThat(result, matchesPattern("^[" + Pattern.quote(SpecialCharacterData.SPECIAL_CHARACTERS) + "]+$"));
    verify(passwordEncoder, times(1)).encode(org.mockito.ArgumentMatchers.any(CharSequence.class));
    verifyNoMoreInteractions(passwordEncoder);
  }
  
  @Test
  public void whenAllSecurityPropertiesProvied_thenPasswordContainsAllProperties() {
    final SecurityProperties.Password password = new SecurityProperties.Password();
    final int numLower = 1;
    final int numUpper = 1;
    final int numDigits = 1;
    final int numSpecial = 1;
    final int maxLength = 15;
    final SecurityProperties securityProperties = new SecurityProperties();
    securityProperties.setPassword(password);
    
    password.setNumberLowerCase(numLower);
    password.setNumberUpperCase(numUpper);
    password.setNumberDigits(numDigits);
    password.setNumberSpecial(numSpecial);
    password.setMaxLength(maxLength);
    password.setMinLength(3);
    
    final PasswordGenerator generator = 
        new PasswordGeneratorImpl(securityProperties, passwordEncoder);
    
    final String result = generator.generate();
    
    assertThat(result.length(), equalTo(maxLength));
    assertThat(result, matchesPattern(".*[a-z].*"));
    assertThat(result, matchesPattern(".*[A-Z].*"));
    assertThat(result, matchesPattern(".*\\d.*"));
    assertThat(result, matchesPattern(".*[" + Pattern.quote(SpecialCharacterData.SPECIAL_CHARACTERS) + "].*"));
    verify(passwordEncoder, times(1)).encode(org.mockito.ArgumentMatchers.any(CharSequence.class));
    verifyNoMoreInteractions(passwordEncoder);
  }
}
