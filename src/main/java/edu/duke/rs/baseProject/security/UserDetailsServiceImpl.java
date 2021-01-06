package edu.duke.rs.baseProject.security;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.security.password.PasswordExpirationStrategy;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Profile("!samlSecurity")
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
	private transient final UserRepository userRepository;
	private transient final PasswordExpirationStrategy passwordExpirationStrategy;
	private transient final LoginAttemptService loginAttemptService;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Logging in user {}", username);
    
    // see if blocked by ip
    if (this.loginAttemptService.isClientIpBlocked()) {
      throw new UsernameNotFoundException("blocked");
    }
    
		final User user = this.userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
		
		return new AppPrincipal(user, passwordExpirationStrategy.isPasswordExpired(user), loginAttemptService.isBlocked(user));
	}
}
