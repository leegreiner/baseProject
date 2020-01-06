package edu.duke.rs.baseProject.security;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;

import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
  private transient final UserRepository userRepository;
  private final int numberOfLoginAttemptFailuresBeforeTemporaryLock;
  private final int temporaryLockSeconds;
  
  public LoginAttemptServiceImpl(final UserRepository userRepository, final SecurityProperties securityProperties) {
    this.userRepository = userRepository;
    this.numberOfLoginAttemptFailuresBeforeTemporaryLock = securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock();
    this.temporaryLockSeconds = securityProperties.getTemporaryLockSeconds();
  }
  
  @Override
  @Transactional
  public void loginFailed(@NotBlank String key) {
    log.debug("Login failed for " + key);
    
    Optional<User> userOptional = this.userRepository.findByUsernameIgnoreCase(key);
    
    if (userOptional.isPresent()) {
      final User user = userOptional.get();
      
      user.setLastInvalidLoginAttempt(LocalDateTime.now());
      
      if (! this.isBlocked(user)) {
        // brute force may exceed size of data structure. don't update after temp lock
        user.setInvalidLoginAttempts(user.getInvalidLoginAttempts() == null ? 1 : user.getInvalidLoginAttempts() + 1);
      }
    }
  }

  @Override
  @Transactional
  public void loginSucceeded(@NotBlank final String key) {
    log.debug("Login succeeded for " + key);
    Optional<User> userOptional = this.userRepository.findByUsernameIgnoreCase(key);
    
    if (userOptional.isPresent()) {
      final User user = userOptional.get();
      user.setLastLoggedIn(LocalDateTime.now());
      user.setInvalidLoginAttempts(null);
      user.setLastInvalidLoginAttempt(null);
    }
  }

  @Override
  public boolean isBlocked(@NotNull User user) {
    log.debug("Checking to see if user account temporarily disabled for " + user.getUsername());
    
    if (user.getLastInvalidLoginAttempt() != null && user.getInvalidLoginAttempts() != null) {
      if (user.getLastInvalidLoginAttempt().plusSeconds(this.temporaryLockSeconds).isAfter(LocalDateTime.now())
          && user.getInvalidLoginAttempts() >= this.numberOfLoginAttemptFailuresBeforeTemporaryLock) {
        return true;
      }
    }
    
    return false;
  }
}
