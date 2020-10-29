package edu.duke.rs.baseProject.security;

import java.time.LocalDateTime;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.cache.Cache;
import org.springframework.cache.Cache.ValueWrapper;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Service;

import edu.duke.rs.baseProject.config.CacheConfig;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class LoginAttemptServiceImpl implements LoginAttemptService {
  private transient final UserRepository userRepository;
  private final int numberOfLoginAttemptFailuresBeforeTemporaryLock;
  private final int temporaryLockSeconds;
  private final HttpServletRequest httpServletRequest;
  private final Cache bruteForceLoginCache;
  private final int maxBruteForceAttempts;
  
  public LoginAttemptServiceImpl(final UserRepository userRepository, final HttpServletRequest httpServletRequest, 
      final CacheManager cacheManager, final SecurityProperties securityProperties) {
    this.userRepository = userRepository;
    this.httpServletRequest = httpServletRequest;
    this.bruteForceLoginCache = cacheManager.getCache(CacheConfig.BRUTE_FORCE_AUTHENTICATION_CACHE);
    this.maxBruteForceAttempts = securityProperties.getMaxBruteForceAttempts();
    this.numberOfLoginAttemptFailuresBeforeTemporaryLock = securityProperties.getNumberOfLoginAttemptFailuresBeforeTemporaryLock();
    this.temporaryLockSeconds = securityProperties.getTemporaryLockSeconds();
  }
  
  @Override
  @Transactional
  public void loginFailed(@NotBlank String username) {
    log.debug("Login failed for " + username);
    
    this.userRepository.findByUsernameIgnoreCase(username).ifPresentOrElse((user) -> {
      user.setLastInvalidLoginAttempt(LocalDateTime.now());
      
      if (! this.isBlocked(user)) {
        // brute force may exceed size of data structure. don't update after temp lock
        user.setInvalidLoginAttempts(user.getInvalidLoginAttempts() == null ? 1 : user.getInvalidLoginAttempts() + 1);
      }
    }, () -> {
      processUnknownUser(username);
    });
  }

  @Override
  @Transactional
  public void loginSucceeded(@NotBlank final String username) {
    log.debug("Login succeeded for " + username);
    
    this.userRepository.findByUsernameIgnoreCase(username).ifPresent((user) -> {
      user.setLastLoggedIn(LocalDateTime.now());
      user.setInvalidLoginAttempts(null);
      user.setLastInvalidLoginAttempt(null);
      this.bruteForceLoginCache.evictIfPresent(getClientIp());
    });
  }
  
  @Override
  public boolean isBlocked(@NotNull final User user) { 
    if (user.getLastInvalidLoginAttempt() != null && user.getInvalidLoginAttempts() != null) {
      if (user.getLastInvalidLoginAttempt().plusSeconds(this.temporaryLockSeconds).isAfter(LocalDateTime.now())
          && user.getInvalidLoginAttempts() >= this.numberOfLoginAttemptFailuresBeforeTemporaryLock) {
        log.debug("User account temporarily disabled for " + user.getUsername());
        return true;
      }
    }
    
    return false;
  }
  
  @Override
  public boolean isClientIpBlocked() {
    final String clientIp = getClientIp();
    
    final ValueWrapper valueWrapper = this.bruteForceLoginCache.get(clientIp);
    
    if (valueWrapper != null) {
      final Integer attempts = (Integer) valueWrapper.get();
      
      if (attempts != null && attempts >= this.maxBruteForceAttempts) {
        log.debug("Client IP is blocked: " + clientIp);
        return true;
      }
    }
    
    return false;
  }
  
  private void processUnknownUser(final String username) {
    final String clientIp = getClientIp();
    
    final ValueWrapper valueWrapper = this.bruteForceLoginCache.get(clientIp);
    
    if (valueWrapper == null) {
      this.bruteForceLoginCache.put(clientIp, Integer.valueOf(1));
    } else {
      Integer attempts = (Integer) valueWrapper.get();
      
      if (attempts != null && attempts < this.maxBruteForceAttempts) {
        if (++attempts == this.maxBruteForceAttempts) {
          log.warn("Possible brute for authentication attempt for " + username + " from address " + clientIp);
        }
        this.bruteForceLoginCache.put(clientIp, attempts);
      }
    }
  }
  
  private String getClientIp() {
    final String clientIps = this.httpServletRequest.getHeader("X-Forwarded-For");
    
    return clientIps == null ? this.httpServletRequest.getRemoteAddr() : clientIps.split(",")[0];
  }
}
