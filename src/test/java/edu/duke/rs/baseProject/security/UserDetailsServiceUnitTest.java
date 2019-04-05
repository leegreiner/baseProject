package edu.duke.rs.baseProject.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

public class UserDetailsServiceUnitTest {
	@Mock
	private UserRepository userRepository;
	@InjectMocks
	private UserDetailsServiceImpl userDetailsService;
	
	@Before
	public void inint() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test(expected = UsernameNotFoundException.class)
	public void whenUserNotFound_thenUsernameNotFoundExceptionThrown() {
		when(userRepository.findByUserNameIgnoreCase(any(String.class)))
			.thenReturn(Optional.empty());
		
		userDetailsService.loadUserByUsername("abc");
	}
	
	@Test
	public void whenUserFound_thenUserDetailsReturned() {
		final Role role = new Role(RoleName.USER);
		final Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		final User user = new User("johnsmith", "johnspassword", roles);
		when(userRepository.findByUserNameIgnoreCase(user.getUserName()))
			.thenReturn(Optional.of(user));
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUserName());
		
		assertThat(userDetails.getUsername(), equalToIgnoringCase(user.getUserName()));
		assertThat(userDetails.getPassword(), equalToIgnoringCase(user.getPassword()));
		assertThat(userDetails.getAuthorities().size(), equalTo(1));
		assertThat(user.getLastLoggedIn(), notNullValue());
		
		verify(userRepository, times(1)).findByUserNameIgnoreCase(user.getUserName());
		verifyNoMoreInteractions(userRepository);
	}
}
