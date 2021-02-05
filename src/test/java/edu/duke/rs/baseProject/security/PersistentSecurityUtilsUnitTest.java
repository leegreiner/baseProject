package edu.duke.rs.baseProject.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

public class PersistentSecurityUtilsUnitTest {
  private static final String PASSWORD = "abc123ABC";
  private static final String ENCODED_PASSWORD = "asdfasdf";
  @Mock
  private Authentication authentication;
  @Mock
  private SecurityContext securityContext;
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private User user;
  private PersistentSecurityUtils securityUtils;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    securityUtils = new PersistentSecurityUtils(userRepository, passwordEncoder);
    SecurityContextHolder.setContext(securityContext);
  }
  
  @Test
  public void whenPrincipalNotFound_thenPasswordDoesNotMatch() {
    when(securityContext.getAuthentication()).thenReturn(null);
    
    assertThat(securityUtils.currentUserPasswordMatches(PASSWORD), equalTo(false));
  }
  
  @Test
  public void whenUserNotFound_thenPasswordDoesNotMatch() {
    final Long userId = Long.valueOf(1);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(appPrincipal);
    when(appPrincipal.getUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.empty());
    
    assertThrows(IllegalArgumentException.class, () -> this.securityUtils.currentUserPasswordMatches(PASSWORD),
        "error.principalNotFound");
    
    verify(userRepository, times(1)).findById(userId);
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenPasswordMatches_thenCurrentUerPasswordMatchesIsTrue() {
    final Long userId = Long.valueOf(1);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(appPrincipal);
    when(appPrincipal.getUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(user.getPassword()).thenReturn(ENCODED_PASSWORD);
    when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(true);
    
    assertThat(this.securityUtils.currentUserPasswordMatches(PASSWORD)).isEqualTo(true);
    
    verify(userRepository, times(1)).findById(userId);
    verify(passwordEncoder, times(1)).matches(PASSWORD, ENCODED_PASSWORD);
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(passwordEncoder);
  }
  
  @Test
  public void whenPasswordDoesntMatch_thenCurrentUerPasswordMatchesIsFalse() {
    final Long userId = Long.valueOf(1);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(appPrincipal);
    when(appPrincipal.getUserId()).thenReturn(userId);
    when(userRepository.findById(userId)).thenReturn(Optional.of(user));
    when(user.getPassword()).thenReturn(ENCODED_PASSWORD);
    when(passwordEncoder.matches(PASSWORD, ENCODED_PASSWORD)).thenReturn(false);
    
    assertThat(this.securityUtils.currentUserPasswordMatches(PASSWORD)).isEqualTo(false);
    
    verify(userRepository, times(1)).findById(userId);
    verify(passwordEncoder, times(1)).matches(PASSWORD, ENCODED_PASSWORD);
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(passwordEncoder);
  }
}
