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

import java.util.HashSet;
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
import edu.duke.rs.baseProject.role.Privilege;
import edu.duke.rs.baseProject.role.PrivilegeName;
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
	
	@BeforeEach 
	public void inint() {
		MockitoAnnotations.openMocks(this);
		when(passwordExpirationStrategy.isPasswordExpired(any(User.class))).thenReturn(false);
	}
	
	@Test
	public void whenLoginAttemptBlocked_thenUsernameNotFoundExceptionThrown() {
	  when(loginAttemptService.isClientIpBlocked()).thenReturn(true);
	  assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername("abc"));
	}
	
	@Test
	public void whenUserNotFound_thenUsernameNotFoundExceptionThrown() {
	  final String username = "abc";
		when(userRepository.getByUsernameIgnoreCase(any(String.class)))
			.thenReturn(Optional.empty());
		
		assertThrows(UsernameNotFoundException.class, () -> userDetailsService.loadUserByUsername(username));
		verify(loginAttemptService, times(1)).isClientIpBlocked();
    verify(userRepository, times(1)).getByUsernameIgnoreCase(username);
    verifyNoMoreInteractions(loginAttemptService);
    verifyNoMoreInteractions(userRepository);
	}
	
	@Test
	public void whenUserFound_thenUserDetailsReturned() {
	  final Privilege privilege = new Privilege(PrivilegeName.VIEW_USERS);
		final Role role = new Role(RoleName.USER);
		role.setPrivileges(Set.of(privilege));
		final Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		final User user = new User("johnsmith", "johnspassword", "John", "Smith","johnSmith@gmail.com", roles);
		when(userRepository.getByUsernameIgnoreCase(user.getUsername()))
			.thenReturn(Optional.of(user));
		
		final UserDetails userDetails = userDetailsService.loadUserByUsername(user.getUsername());
		
		assertThat(userDetails.getUsername(), equalToIgnoringCase(user.getUsername()));
		assertThat(userDetails.getPassword(), equalToIgnoringCase(user.getPassword()));
		assertThat(userDetails.getAuthorities().size(), equalTo(2));
		
		final List<String> authorityNames = List.of(privilege.getName().name(), AppPrincipal.ROLE_PREFIX + role.getName().name());
		
		userDetails.getAuthorities().stream().forEach(authority -> {
		  assertThat(authorityNames, hasItem(authority.getAuthority()));
		});
		
		verify(loginAttemptService, times(1)).isClientIpBlocked();
		verify(loginAttemptService, times(1)).isBlocked(user);
		verify(userRepository, times(1)).getByUsernameIgnoreCase(user.getUsername());
		verifyNoMoreInteractions(loginAttemptService);
		verifyNoMoreInteractions(userRepository);
	}
}
