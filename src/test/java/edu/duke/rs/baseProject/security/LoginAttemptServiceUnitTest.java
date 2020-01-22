package edu.duke.rs.baseProject.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

public class LoginAttemptServiceUnitTest {
  @Mock
  private SecurityProperties securityProperties;
  @Mock
  private UserRepository userRepository;
  private LoginAttemptServiceImpl loginAttemptService;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    when(securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock()).thenReturn(3);
    when(securityProperties.getTemporaryLockSeconds()).thenReturn(300);
    loginAttemptService = new LoginAttemptServiceImpl(userRepository, securityProperties);
  }
  
  @Test
  public void whenLoginFailedAndUserNotFound_thenNothingIsDone() {
    final String userName = "abc";
    when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.empty());
    
    this.loginAttemptService.loginFailed(userName);
    
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenLoginFailedAndUserFoundAndNotBlocked_thenInvalidAttemptDataUpdated() throws Exception {
    final String userName = "abc";
    final User user = new User();
    when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.of(user));
    
    this.loginAttemptService.loginFailed(userName);
    
    assertThat(user.getLastInvalidLoginAttempt(), notNullValue());
    assertThat(user.getInvalidLoginAttempts(), equalTo(1));
    assertThat(user.getLastLoggedIn(), nullValue());
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
    verifyNoMoreInteractions(userRepository);
    
    reset(userRepository);
    when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.of(user));
    final LocalDateTime previousLastInvalidLoginAttempt = user.getLastInvalidLoginAttempt();
    
    Thread.sleep(1000L);
    
    this.loginAttemptService.loginFailed(userName);
    
    assertThat(user.getLastInvalidLoginAttempt().isAfter(previousLastInvalidLoginAttempt), is(true));
    assertThat(user.getInvalidLoginAttempts(), equalTo(2));
    assertThat(user.getLastLoggedIn(), nullValue());
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenLoginFailedAndUserFoundAndBlocked_thenInvalidAttemptDataUpdated() {
    final String userName = "abc";
    final LocalDateTime previousLastInvalidLoginAttempt = LocalDateTime.now().minusSeconds(1);
    final User user = new User();
    user.setInvalidLoginAttempts(securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock());
    user.setLastInvalidLoginAttempt(previousLastInvalidLoginAttempt);
    when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.of(user));
    when(securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock()).thenReturn(3);
    when(securityProperties.getTemporaryLockSeconds()).thenReturn(300);
    
    this.loginAttemptService.loginFailed(userName);
    
    assertThat(user.getLastInvalidLoginAttempt().isAfter(previousLastInvalidLoginAttempt), is(true));
    assertThat(user.getInvalidLoginAttempts(), equalTo(securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock()));
    assertThat(user.getLastLoggedIn(), nullValue());
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
    verifyNoMoreInteractions(userRepository);
    
  
  }
  
  @Test
  public void whenLoginSucceedsAndUserNotFound_thenNothingIsDone() {
    final String userName = "abc";
    when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.empty());
    
    this.loginAttemptService.loginSucceeded(userName);
    
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenLoginSucceedsAndUserFound_thenInvalidAttemptDataClearedAndLastLoginSet() {
    final String userName = "abc";
    final User user = new User();
    user.setLastInvalidLoginAttempt(LocalDateTime.now());
    user.setInvalidLoginAttempts(2);
    when(userRepository.findByUsernameIgnoreCase(userName)).thenReturn(Optional.of(user));
    
    this.loginAttemptService.loginSucceeded(userName);
    
    assertThat(user.getLastInvalidLoginAttempt(), nullValue());
    assertThat(user.getInvalidLoginAttempts(), nullValue());
    assertThat(user.getLastLoggedIn(), notNullValue());
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userName);
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenNoInvalidAttemptDate_thenIsBlockedIsFalse() {
    final User user = new User();
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
    
    assertThat(loginAttemptService.isBlocked(user), is(false));
  }
  
  @Test
  public void whenMaxAttemptsReachedAndLastLoginAttemptWithinThreshold_thenIsBlockedIsTrue() {
    final User user = new User();
    user.setLastInvalidLoginAttempt(LocalDateTime.now());
    user.setInvalidLoginAttempts(securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock());
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
    
    assertThat(loginAttemptService.isBlocked(user), is(true));
  }
  
  @Test
  public void whenMaxAttemptsReachedAndLastLoginAttemptNotWithinThreshold_thenIsBlockedIsTrue() {
    final User user = new User();
    user.setLastInvalidLoginAttempt(LocalDateTime.now().minusSeconds(securityProperties.getTemporaryLockSeconds() + 1));
    user.setInvalidLoginAttempts(securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock());
    when(userRepository.findByUsernameIgnoreCase(any(String.class))).thenReturn(Optional.of(user));
    
    assertThat(loginAttemptService.isBlocked(user), is(false));
  }
}
