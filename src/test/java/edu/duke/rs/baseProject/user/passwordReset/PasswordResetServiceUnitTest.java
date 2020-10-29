package edu.duke.rs.baseProject.user.passwordReset;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;

import edu.duke.rs.baseProject.config.ApplicationProperties;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.user.PasswordHistory;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

public class PasswordResetServiceUnitTest {
  private static final Long EXPIRATION_DAYS = Long.valueOf(2);
  private static final Integer PASSWORD_HISTORY_SIZE = 2;
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private UserRepository userRepository;
  @Mock
  private PasswordEncoder passwordEncoder;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private SecurityUtils securityUtils;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private ApplicationProperties applicationProperties;
  private PasswordResetServiceImpl service;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    when(applicationProperties.getSecurity().getPassword().getResetPasswordExpirationDays()).thenReturn(EXPIRATION_DAYS);
    when(applicationProperties.getSecurity().getPassword().getHistorySize()).thenReturn(PASSWORD_HISTORY_SIZE);
    service = new PasswordResetServiceImpl(userRepository, passwordEncoder, eventPublisher, securityUtils, applicationProperties);
  }
  
  @Test
  public void whenEmailNotFound_thenInitiatePasswordResetThrowsNotFoundException() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.empty());
    
    assertThrows(NotFoundException.class, () -> this.service.initiatePasswordReset(dto));
  }
  
  @Test
  public void whenEmailEnteredDoesntMatchLoggedInUser_thenInitiatePasswordResetThrowsConstraintViolationException() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();
    final User user = new User();
    user.setEmail(dto.getEmail());

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(user));
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getEmail()).thenReturn("m" + dto.getEmail() + "");
    
    assertThrows(ConstraintViolationException.class, () -> this.service.initiatePasswordReset(dto));
  }
  
  @Test
  public void whenUserLoggedInInitiatePasswordResetSucceeds_thenPasswordResetFieldsSetAndEventTriggered() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();
    final User user = new User();
    user.setEmail(dto.getEmail());

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(user));
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getEmail()).thenReturn(dto.getEmail());
    
    this.service.initiatePasswordReset(dto);
    
    assertThat(user.getPasswordChangeId(), notNullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), notNullValue());
    
    verify(userRepository, times(1)).findByEmailIgnoreCase(dto.getEmail());
    verify(eventPublisher, times(1)).publishEvent(any(PasswordResetInitiatedEvent.class));
    verify(securityUtils, times(1)).getPrincipal();
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(eventPublisher);
    verifyNoMoreInteractions(securityUtils);
  }
  
  @Test
  public void whenUserNotLoggedInInitiatePasswordResetSucceeds_thenPasswordResetFieldsSetAndEventTriggered() {
    final InitiatePasswordResetDto dto = createInitiatePasswordResetDto();
    final User user = new User();
    user.setEmail(dto.getEmail());

    when(this.userRepository.findByEmailIgnoreCase(dto.getEmail())).thenReturn(Optional.of(user));
    when(securityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    this.service.initiatePasswordReset(dto);
    
    assertThat(user.getPasswordChangeId(), notNullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), notNullValue());
    
    verify(userRepository, times(1)).findByEmailIgnoreCase(dto.getEmail());
    verify(eventPublisher, times(1)).publishEvent(any(PasswordResetInitiatedEvent.class));
    verify(securityUtils, times(1)).getPrincipal();
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(eventPublisher);
    verifyNoMoreInteractions(securityUtils);
  }
  
  @Test
  public void whenUserNotFoundUsingResetId_thenProcessPasswordResetThrowsNotFoundException() {
    final PasswordResetDto dto = createPasswordResetDto();
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.empty());
    
    assertThrows(NotFoundException.class, () -> this.service.processPasswordReset(dto));
  }
  
  @Test
  public void whenResetIdExpired_thenProcessPasswordResetThrowsNotFoundException() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now().minusDays(EXPIRATION_DAYS + 1));
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    
    assertThrows(NotFoundException.class, () -> this.service.processPasswordReset(dto));
  }
  
  @Test
  public void whenUsernameEnteredDoesntMatchUser_thenProcessPasswordResetThrowsConstraintViolationException() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUsername(dto.getUsername() + "m");
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    
    assertThrows(ConstraintViolationException.class, () -> this.service.processPasswordReset(dto));
  }
  
  @Test
  public void whenNewPasswordMatchesOldPassword_thenProcessPasswordResetThrowsConstraintViolationException() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword());
    user.addPasswordHistory(new PasswordHistory(user.getPassword()));
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    when(this.passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

    assertThrows(ConstraintViolationException.class, () -> this.service.processPasswordReset(dto));
  }
  
  @Test
  public void whenCurrentUserLoggedInAndChangingDifferntAccount_thenProcessPasswordResetThrowsConstraintViolationException() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword() + "a");
    user.setId(Long.valueOf(1));
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getUserId()).thenReturn(user.getId() + 1);
    
    assertThrows(ConstraintViolationException.class, () -> this.service.processPasswordReset(dto));
  }
  
  @Test
  public void whenPasswordResetWithHistory_thenNewPasswordCreatedAndResetFieldsCleared() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword() + "a");
    user.setId(Long.valueOf(1));
    user.addPasswordHistory(new PasswordHistory(user.getPassword()));
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getUserId()).thenReturn(user.getId());
    when(this.passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);
    when(this.passwordEncoder.encode(dto.getPassword())).thenReturn(dto.getPassword() + "m");
    
    this.service.processPasswordReset(dto);
    
    assertThat(user.getLastPasswordChange(), notNullValue());
    assertThat(user.getPassword(), equalTo(dto.getPassword() + "m"));
    assertThat(user.getPasswordChangeId(), nullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), nullValue());
    assertThat(user.getPasswordHistory().size(), equalTo(2));
    assertThat(user.getPasswordHistory().stream()
        .filter(passwordHistory -> passwordHistory.getPassword().equals(dto.getPassword() + "m")).count(), equalTo(1L));
    
    verify(this.userRepository, times(1)).findByPasswordChangeId(dto.getPasswordChangeId());
    verify(this.passwordEncoder, times(1)).matches(dto.getPassword(), dto.getPassword() + "a");
    verify(this.passwordEncoder, times(1)).encode(dto.getPassword());
    verifyNoMoreInteractions(this.userRepository);
    verifyNoMoreInteractions(this.passwordEncoder);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  @Test
  public void whenPasswordResetAndNoHistory_thenNewPasswordCreatedAndResetFieldsCleared() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword() + "a");
    user.setId(Long.valueOf(1));
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getUserId()).thenReturn(user.getId());
    when(this.passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);
    when(this.passwordEncoder.encode(dto.getPassword())).thenReturn(dto.getPassword() + "m");
    
    this.service.processPasswordReset(dto);
    
    assertThat(user.getLastPasswordChange(), notNullValue());
    assertThat(user.getPassword(), equalTo(dto.getPassword() + "m"));
    assertThat(user.getPasswordChangeId(), nullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), nullValue());
    assertThat(user.getPasswordHistory().size(), equalTo(1));
    assertThat(user.getPasswordHistory().stream()
        .filter(passwordHistory -> passwordHistory.getPassword().equals(dto.getPassword() + "m")).count(), equalTo(1L));
    
    verify(this.userRepository, times(1)).findByPasswordChangeId(dto.getPasswordChangeId());
    verify(this.passwordEncoder, times(1)).encode(dto.getPassword());
    verifyNoMoreInteractions(this.userRepository);
    verifyNoMoreInteractions(this.passwordEncoder);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  @Test
  public void whenPasswordResetAndMaxHistory_thenNewPasswordCreatedResetFieldsClearedAndOldestHistoryEntryRemoved() {
    final PasswordResetDto dto = createPasswordResetDto();
    final User user = new User();
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user.setUsername(dto.getUsername());
    user.setPassword(dto.getPassword() + "a");
    user.setId(Long.valueOf(1));

    for (int i = 0; i < PASSWORD_HISTORY_SIZE; i++) {
      final PasswordHistory passwordHistory = new PasswordHistory(dto.getPassword() + i);
      passwordHistory.setLastModifiedDate(LocalDateTime.now().plusMinutes(i));
      user.addPasswordHistory(passwordHistory);
    }
    
    when(this.userRepository.findByPasswordChangeId(dto.getPasswordChangeId())).thenReturn(Optional.of(user));
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getUserId()).thenReturn(user.getId());
    when(this.passwordEncoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);
    when(this.passwordEncoder.encode(dto.getPassword())).thenReturn(dto.getPassword() + "m");
    
    this.service.processPasswordReset(dto);
    
    assertThat(user.getLastPasswordChange(), notNullValue());
    assertThat(user.getPassword(), equalTo(dto.getPassword() + "m"));
    assertThat(user.getPasswordChangeId(), nullValue());
    assertThat(user.getPasswordChangeIdCreationTime(), nullValue());
    assertThat(user.getPasswordHistory().size(), equalTo(PASSWORD_HISTORY_SIZE));
    assertThat(user.getPasswordHistory().stream()
        .filter(passwordHistory -> passwordHistory.getPassword().equals(dto.getPassword() + "m")).count(), equalTo(1L));
    
    verify(this.userRepository, times(1)).findByPasswordChangeId(dto.getPasswordChangeId());
    
    for (int i = 0; i < PASSWORD_HISTORY_SIZE; i++) {
      verify(this.passwordEncoder, times(1)).matches(dto.getPassword(), dto.getPassword() + i);
    }
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
    dto.setUsername("johnsmith");
    
    return dto;
  }
}
