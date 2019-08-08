package edu.duke.rs.baseProject.security;

import java.time.LocalDateTime;

import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Profile("!samlSecurity")
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
	private transient final UserRepository userRepository;
	private transient final PasswordExpirationStrategy passwordExpirationStrategy;
	
	public UserDetailsServiceImpl(final UserRepository userRepository,final PasswordExpirationStrategy passwordExpirationStrategy) {
		this.userRepository = userRepository;
		this.passwordExpirationStrategy = passwordExpirationStrategy;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		log.debug("Logging in user " + userName);
		final User user = this.userRepository.findByUserNameIgnoreCase(userName)
				.orElseThrow(() -> new UsernameNotFoundException("User " + userName + " not found"));

		user.setLastLoggedIn(LocalDateTime.now());
		
		return new AppPrincipal(user, passwordExpirationStrategy.isPasswordExpired(user));
	}
}
