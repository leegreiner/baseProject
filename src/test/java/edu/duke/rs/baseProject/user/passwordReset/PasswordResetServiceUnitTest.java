package edu.duke.rs.baseProject.user.passwordReset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.verification.VerificationModeFactory;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*"})
@PrepareForTest(SecurityUtils.class)
public class PasswordResetServiceUnitTest {
  private static final Long EXPIRATION_DAYS = Long.valueOf(2);
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  private PasswordResetServiceImpl service;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    mockStatic(SecurityUtils.class);
    service = new PasswordResetServiceImpl(userRepository, passwordEncoder, eventPublisher);
    service.setResetPasswordExpirationDays(EXPIRATION_DAYS);
  }
  
  @Test(expected = NotFoundException.class)
  public void whenEmailNotFound_thenInitiatePasswordResetThrowsNotFoundException() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.empty());
    
    this.service.initiatePasswordReset(dto);
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void whenEmailEnteredDoesntMatchLoggedInUser_thenInitiatePasswordResetThrowsConstraintViolationException() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();
    final User user = new User();
    user.setEmail(dto.getEmail());

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(user));
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getEmail()).thenReturn("m" + dto.getEmail() + "");
    
    this.service.initiatePasswordReset(dto);
  }
  
  @Test
  public void whenUserLoggedInInitiatePasswordResetSucceeds_thenPasswordResetFieldsSetAndEventTriggered() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();
    final User user = new User();
    user.setEmail(dto.getEmail());

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(user));
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getEmail()).thenReturn(dto.getEmail());
    
    this.service.initiatePasswordReset(dto);
    
    assertThat(user.getPasswordChangeId(), notNullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), notNullValue());
    
    verify(userRepository, times(1)).findByEmailIgnoreCase(dto.getEmail());
    verify(eventPublisher, times(1)).publishEvent(any(PasswordResetInitiatedEvent.class));
    PowerMockito.verifyStatic(SecurityUtils.class, VerificationModeFactory.times(1));
    SecurityUtils.getPrincipal();
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(eventPublisher);
    PowerMockito.verifyNoMoreInteractions(SecurityUtils.class);
  }
  
  @Test
  public void whenUserNotLoggedInInitiatePasswordResetSucceeds_thenPasswordResetFieldsSetAndEventTriggered() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();
    final User user = new User();
    user.setEmail(dto.getEmail());

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(user));
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    this.service.initiatePasswordReset(dto);
    
    assertThat(user.getPasswordChangeId(), notNullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), notNullValue());
    
    verify(userRepository, times(1)).findByEmailIgnoreCase(dto.getEmail());
    verify(eventPublisher, times(1)).publishEvent(any(PasswordResetInitiatedEvent.class));
    PowerMockito.verifyStatic(SecurityUtils.class, VerificationModeFactory.times(1));
    SecurityUtils.getPrincipal();
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(eventPublisher);
    PowerMockito.verifyNoMoreInteractions(SecurityUtils.class);
  }
  
  @Test(expected = NotFoundException.class)
  public void whenUserNotFoundUsingResetId_thenProcessPasswordResetThrowsNotFoundException() {
    final PasswordResetDto dto = createPasswordResetDto();
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.empty());
    
    this.service.processPasswordReset(dto);
  }
  
  @Test(expected = NotFoundException.class)
  public void whenResetIdExpired_thenProcessPasswordResetThrowsNotFoundException() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now().minusDays(EXPIRATION_DAYS + 1));
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    
    this.service.processPasswordReset(dto);
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void whenUserNameEnteredDoesntMatchUser_thenProcessPasswordResetThrowsConstraintViolationException() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUserName(dto.getUserName() + "m");
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    
    this.service.processPasswordReset(dto);
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void whenNewPasswordMatchesOldPassword_thenProcessPasswordResetThrowsConstraintViolationException() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUserName(dto.getUserName());
    user.setPassword(dto.getPassword());
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    when(this.passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);
    
    this.service.processPasswordReset(dto);
  }
  
  @Test
  public void whenPasswordReset_thenNewPasswordCreatedAndResetFieldsCleared() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUserName(dto.getUserName());
    user.setPassword(dto.getPassword() + "a");
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    when(this.passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);
    when(this.passwordEncoder.encode(dto.getPassword())).thenReturn(dto.getPassword() + "m");
    
    this.service.processPasswordReset(dto);
    
    assertThat(user.getLastPasswordChange(), notNullValue());
    assertThat(user.getPassword(), equalTo(dto.getPassword() + "m"));
    assertThat(user.getPasswordChangeId(), nullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), nullValue());
    
    verify(this.userRepository, times(1)).findByPasswordChangeId(dto.getPasswordChangeId());
    verify(this.passwordEncoder, times(1)).matches(dto.getPassword(), dto.getPassword() + "a");
    verify(this.passwordEncoder, times(1)).encode(dto.getPassword());
    verifyNoMoreInteractions(this.userRepository);
    verifyNoMoreInteractions(this.passwordEncoder);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  @Test
  public void whenExpirePasswordResetIds_thenDatabaseUpdated() {
    this.service.expirePasswordResetIds();
    
    verify(this.userRepository, times(1)).expirePasswordChangeIds(any(LocalDateTime.class));
    
    verifyNoMoreInteractions(this.userRepository);
    verifyNoMoreInteractions(this.passwordEncoder);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  private static InitiatePasswordResetDto createInitiatePasswordResetDto() {
    final InitiatePasswordResetDto dto = new InitiatePasswordResetDto();
    dto.setEmail("abc@123.com");
 
    return dto;
  }
  
  private static PasswordResetDto createPasswordResetDto() {
    final PasswordResetDto dto = new PasswordResetDto();
    dto.setConfirmPassword("abc123ABC");
    dto.setPassword(dto.getConfirmPassword());
    dto.setPasswordChangeId(UUID.randomUUID());
    dto.setUserName("johnsmith");
    
    return dto;
  }
}
