package edu.duke.rs.baseProject.security;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service("userDetailsService")
public class UserDetailsServiceImpl implements UserDetailsService {
	private transient final UserRepository userRepository;
	
	public UserDetailsServiceImpl(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
		log.debug("Logging in user " + userName);
		final User user = this.userRepository.findByUserNameIgnoreCase(userName);
		
		if (user == null) {
			throw new UsernameNotFoundException("User " + userName + " not found");
		}
		
		user.setLastLoggedIn(LocalDateTime.now());
		
		final String[] roleNames = user.getRoles().stream()
				.map( role -> {
					return role.getName().name();
				})
				.toArray(String[]::new);
		
		return org.springframework.security.core.userdetails.User.builder()
				.accountExpired(false)
				.accountLocked(false)
				.password(user.getPassword())
				.credentialsExpired(false)
				.disabled(false)
				.roles(roleNames)
				.username(user.getUserName())
				.build();
	}
}
