package edu.duke.rs.baseProject.security;


import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class PersistentSecurityUtils extends SecurityUtils {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  
  public boolean currentUserPasswordMatches(final String rawPassword) {
    final Optional<AppPrincipal> currentPrincipal = this.getPrincipal();
    
    if (currentPrincipal.isEmpty()) {
      return false;
    }
    
    final User currentUser = 
        this.userRepository.findById(currentPrincipal.get().getUserId())
          .orElseThrow(() ->  new IllegalArgumentException("error.principalNotFound"));
    
    return passwordEncoder.matches(rawPassword, currentUser.getPassword());
  }
}
