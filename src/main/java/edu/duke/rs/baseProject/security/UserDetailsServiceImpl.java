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
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Profile("!samlSecurity")
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
	private transient final UserRepository userRepository;
	private transient final PasswordExpirationStrategy passwordExpirationStrategy;
	private transient final LoginAttemptService loginAttemptService;
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.debug("Logging in user " + username);
		final User user = this.userRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new UsernameNotFoundException("User " + username + " not found"));
		
		return new AppPrincipal(user, passwordExpirationStrategy.isPasswordExpired(user), loginAttemptService.isBlocked(user));
	}
}
