package edu.duke.rs.baseProject.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.hasItem;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.duke.rs.baseProject.AbstractBaseTest;
import edu.duke.rs.baseProject.UserBuilder;
import edu.duke.rs.baseProject.UsrBuilder;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.password.PasswordExpirationStrategy;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

public class UserDetailsServiceUnitTest extends AbstractBaseTest {
	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordExpirationStrategy passwordExpirationStrategy;
	@Mock
	private LoginAttemptService loginAttemptService;
	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;
	private UsrBuilder userBuilder = new UserBuilder();
	
	@BeforeEach 
	public void inint() {
		MockitoAnnotations.openMocks(this);
		when(passwordExpirationStrategy.isPasswordExpired(any(User.class))).thenReturn(false);
	}
	
	@Test
	public void whenLoginAttemptBlocked_thenUsernameNotFoundExceptionThrown() {
	  when(loginAttemptService.isClientIpBlocked()).thenReturn(true);
	  assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(easyRandom.nextObject(String.class)));
	}
	
	@Test
	public void whenUserNotFound_thenUsernameNotFoundExceptionThrown() {
	  final String username = easyRandom.nextObject(String.class);
		when(userRepository.getByUsernameIgnoreCase(username))
			.thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
		verify(loginAttemptService, times(1)).isClientIpBlocked();
    verify(userRepository, times(1)).getByUsernameIgnoreCase(username);
    verifyNoMoreInteractions(loginAttemptService);
    verifyNoMoreInteractions(userRepository);
	}
	
	@Test
	public void whenUserFound_thenUserDetailsReturned() {
		final User user = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
		    easyRandom.nextObject(String.class),"johnSmith@gmail.com", Set.of(RoleName.USER));
		
		when(userRepository.getByUsernameIgnoreCase(user.getUsername()))
			.thenReturn(Optional.of(user));
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
		
		assertThat(userDetails.getUsername(), equalToIgnoringCase(user.getUsername()));
		assertThat(userDetails.getPassword(), equalToIgnoringCase(user.getPassword()));
		
		final List<String> expectedAuthorities = new ArrayList<String>();
    for (final Role role : user.getRoles()) {
      expectedAuthorities.add(AppPrincipal.ROLE_PREFIX + role.getName().name());
      role.getPrivileges().forEach(privilege -> expectedAuthorities.add(privilege.getName().name()));
    }
		assertThat(userDetails.getAuthorities().size(), equalTo(expectedAuthorities.size()));
		
		userDetails.getAuthorities().stream().forEach(authority -> {
		  assertThat(expectedAuthorities, hasItem(authority.getAuthority()));
		});
		
		verify(loginAttemptService, times(1)).isClientIpBlocked();
		verify(loginAttemptService, times(1)).isBlocked(user);
		verify(userRepository, times(1)).getByUsernameIgnoreCase(user.getUsername());
		verifyNoMoreInteractions(loginAttemptService);
		verifyNoMoreInteractions(userRepository);
	}
}
