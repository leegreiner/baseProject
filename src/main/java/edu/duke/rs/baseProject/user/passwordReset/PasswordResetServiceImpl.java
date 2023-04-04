package edu.duke.rs.baseProject.user.passwordReset;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.duke.rs.baseProject.config.ApplicationProperties;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.user.PasswordHistory;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

@Service
@Profile("!samlSecurity")
public class PasswordResetServiceImpl implements PasswordResetService {
  private transient final UserRepository userRepository;
  private transient final PasswordEncoder passwordEncoder;
  private transient final ApplicationEventPublisher eventPublisher;
  private transient final SecurityUtils securityUtils;
  private Long resetPasswordExpirationDays;
  private Integer passwordHistorySize;

  public PasswordResetServiceImpl(final UserRepository userRepository,
      final PasswordEncoder passwordEncoder,
      final ApplicationEventPublisher eventPublisher,
      final SecurityUtils securityUtils,
      final ApplicationProperties applicationProperties) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.eventPublisher = eventPublisher;
    this.securityUtils = securityUtils;
    this.resetPasswordExpirationDays = applicationProperties.getSecurity().getPassword().getResetPasswordExpirationDays();
    this.passwordHistorySize = applicationProperties.getSecurity().getPassword().getHistorySize();
  }

  @Override
  @Transactional
  public void initiatePasswordReset(@Valid final InitiatePasswordResetDto passwordResetDto) {
    final User user = this.userRepository.findByEmailIgnoreCase(passwordResetDto.getEmail())
        .orElseThrow(() -> new NotFoundException("error.userNotFound", new Object[] {passwordResetDto.getEmail()}));
   
    final Optional<AppPrincipal> currentUserOptional = securityUtils.getPrincipal();
   
    if (currentUserOptional.isPresent() && 
        ! StringUtils.equalsIgnoreCase(currentUserOptional.get().getEmail(), user.getEmail())) {
      throw new ConstraintViolationException("error.passwordReset.invalidEmail", (Object[])null);
    }
    
    this.initiatePasswordReset(user);
    
    this.eventPublisher.publishEvent(new PasswordResetInitiatedEvent(this, user));
  }
  
  @Override
  public void initiatePasswordReset(final User user) {
    user.setPasswordChangeId(UUID.randomUUID());
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
  }

  @Override
  @Transactional
  public void processPasswordReset(@Valid final PasswordResetDto passwordResetDto) {
    final Optional<User> userOptional = this.userRepository.findByPasswordChangeId(passwordResetDto.getPasswordChangeId());
    
    if (userOptional.isEmpty() ||
        userOptional.get().getPasswordChangeIdCreationTime().plusDays(resetPasswordExpirationDays).isBefore(LocalDateTime.now())) {
      throw new NotFoundException("error.userNotFound", new Object[] {passwordResetDto.getPasswordChangeId()});
    }
    
    final User user = userOptional.get();
    
    final Optional<AppPrincipal> currentUserOptional = securityUtils.getPrincipal();
    
    if (currentUserOptional.isPresent() && ! currentUserOptional.get().getUserId().equals(user.getId())) {
      throw new ConstraintViolationException("error.passwordReset.currentlyLoggedInAsDifferentUser", (Object[])null);
    }
    
    if (! StringUtils.equalsIgnoreCase(user.getUsername(), passwordResetDto.getUsername())) {
      throw new ConstraintViolationException("error.passwordReset.invalidUserName", (Object[])null);
    }

    verifyAgainstAndProcessPasswordHistory(user, passwordResetDto.getPassword());
    
    user.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
    user.setLastPasswordChange(LocalDateTime.now());
    user.setPasswordChangeId(null);
    user.setPasswordChangeIdCreationTime(null);
    user.addPasswordHistory(new PasswordHistory(user.getPassword()));
  }

  @Override
  @Transactional
  public void expirePasswordResetIds() {
    this.userRepository.expirePasswordChangeIds(LocalDateTime.now().minusDays(resetPasswordExpirationDays));
  }
  
  private void verifyAgainstAndProcessPasswordHistory(final User user, final String newPassword) {
    PasswordHistory oldestHistory = null;
    
    for (final PasswordHistory passwordHistory : user.getPasswordHistory()) {
      if (this.passwordEncoder.matches(newPassword, passwordHistory.getPassword())) {
        throw new ConstraintViolationException("error.passwordReset.passwordAlreadyUsed", (Object[])null);
      }
      
      if (oldestHistory == null ||
          passwordHistory.getLastModifiedDate().isBefore(oldestHistory.getLastModifiedDate())) {
        oldestHistory = passwordHistory;
      }
    }
    
    if (oldestHistory != null && user.getPasswordHistory().size() >= this.passwordHistorySize) {
      user.removePasswordHistory(oldestHistory);
    }
  }
}
