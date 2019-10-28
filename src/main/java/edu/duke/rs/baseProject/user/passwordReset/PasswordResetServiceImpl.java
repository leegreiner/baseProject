package edu.duke.rs.baseProject.user.passwordReset;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.Setter;

@Service
@Profile("!samlSecurity")
public class PasswordResetServiceImpl implements PasswordResetService {
  private transient final UserRepository userRepository;
  private transient final PasswordEncoder passwordEncoder;
  private transient final ApplicationEventPublisher eventPublisher;
  @Setter
  @Value("${app.resetPasswordExpirationDays:2}")
  private Long resetPasswordExpirationDays;

  public PasswordResetServiceImpl(final UserRepository userRepository,
      final PasswordEncoder passwordEncoder,
      final ApplicationEventPublisher eventPublisher) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.eventPublisher = eventPublisher;
  }

  @Override
  @Transactional
  public void initiatePasswordReset(@Valid final InitiatePasswordResetDto passwordResetDto) {
    final User user = this.userRepository.findByEmailIgnoreCase(passwordResetDto.getEmail())
        .orElseThrow(() -> new NotFoundException("error.userNotFound", new Object[] {passwordResetDto.getEmail()}));
   
    final Optional<AppPrincipal> currentUserOptional = SecurityUtils.getPrincipal();
   
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
    final Optional<AppPrincipal> currentUserOptional = SecurityUtils.getPrincipal();
    
    if (currentUserOptional.isPresent() && ! currentUserOptional.get().getUserId().equals(user.getId())) {
      throw new ConstraintViolationException("error.passwordReset.currentlyLoggedInAsDifferentUser", (Object[])null);
    }
    
    if (! StringUtils.equalsIgnoreCase(user.getUsername(), passwordResetDto.getUsername())) {
      throw new ConstraintViolationException("error.passwordReset.invalidUserName", (Object[])null);
    }
    
    if (passwordEncoder.matches(passwordResetDto.getPassword(), user.getPassword())) {
      throw new ConstraintViolationException("error.passwordReset.cannotReusePassword", (Object[])null);
    }
    
    user.setPassword(passwordEncoder.encode(passwordResetDto.getPassword()));
    user.setLastPasswordChange(LocalDateTime.now());
    user.setPasswordChangeId(null);
    user.setPasswordChangeIdCreationTime(null);
  }

  @Override
  @Transactional
  public void expirePasswordResetIds() {
    this.userRepository.expirePasswordChangeIds(LocalDateTime.now().minusDays(resetPasswordExpirationDays));
  }
}
