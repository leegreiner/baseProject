package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;

import dev.samstevens.totp.secret.SecretGenerator;
import edu.duke.rs.baseProject.event.CreatedEvent;
import edu.duke.rs.baseProject.event.UpdatedEvent;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.PersistentSecurityUtils;
import edu.duke.rs.baseProject.security.password.PasswordGenerator;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetService;


public class UserServiceUnitTest {
  private static final String CHANGE_REASON = "Need to update";
  private static final String CHANGE_PASSWORD = "abc123ABC";
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RoleRepository roleRepository;
  @Mock
  private PasswordGenerator passwordGenerator;
  @Mock
  private SecretGenerator secretGenerator;
  @Mock
  private PasswordResetService passwordResetService;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private PersistentSecurityUtils securityUtils;
  
  private UserServiceImpl service;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    service = new UserServiceImpl(userRepository, roleRepository, passwordGenerator, secretGenerator,
        passwordResetService, eventPublisher, securityUtils);
  }
  
  @Test
  public void whenNoCurrentUser_thenGetUserProfileThrowsIllegalArgumentExceptionThrown() {
    when(securityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    assertThrows(IllegalArgumentException.class, () -> service.getUserProfile());
  }
  
  @Test
  public void whenUserNotFound_thenGetUserProfileThrowsNotFoundException() {
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.empty());
    
    try {
      service.getUserProfile();
    } catch (final NotFoundException nfe) {
      verify(userRepository, times(1)).findById(appPrincipal.getUserId());
      verifyNoMoreInteractions(userRepository);
      return;
    }
    
    fail("Expected NotFoundException");
  }
  
  @Test
  public void whenUserFound_thenUserAndUserProfileTimeZonesEqual() {
    final User user = new User();
    user.setTimeZone(TimeZone.getTimeZone("GMT"));
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.of(user));
    
    final UserProfile userProfile = service.getUserProfile();
    
    assertThat(userProfile.getTimeZone(), equalTo(user.getTimeZone()));
    verify(userRepository, times(1)).findById(appPrincipal.getUserId());
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenNoCurrentUser_thenUpdateUserProfileThrowsIllegalArgumentExceptionThrown() {
    when(securityUtils.getPrincipal()).thenReturn(Optional.empty());
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("GMT"));
    
    assertThrows(IllegalArgumentException.class, () -> service.updateUserProfile(userProfile), "error.principalNotFound");
  }
  
  @Test
  public void whenUserNotFound_thenUpdateUserProfileThrowsNotFoundException() {
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.empty());
    
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("GMT"));
    
    try {
      service.updateUserProfile(userProfile);
    } catch (final NotFoundException nfe) {
      verify(userRepository, times(1)).findById(appPrincipal.getUserId());
      verifyNoMoreInteractions(userRepository);
      return;
    }
    
    fail("Expected NotFoundException");
  }
  
  @Test
  public void whenUserFound_thenUserTimeZoneEqualsUserProfileTimeZone() {
    final User user = new User();
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.of(user));
    
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("GMT"));
    
    service.updateUserProfile(userProfile);
    
    assertThat(user.getTimeZone(), equalTo(userProfile.getTimeZone()));
    verify(userRepository, times(1)).findById(appPrincipal.getUserId());
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenUserNotFound_thenGetUserThrowsNotFoundException() {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUsername("abc");
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    
    assertThrows(NotFoundException.class, () -> service.getUser(user.getAlternateId()), "error.userNotFound");
  }
  
  @Test
  public void whenUserFound_thenGetUserReturnsUser() {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUsername("abc");
    when(userRepository.findByAlternateId(eq(user.getAlternateId()),
        org.mockito.ArgumentMatchers.any(String.class))).thenReturn(Optional.of(user));
    
    final User retrievedUser = service.getUser(user.getAlternateId());
    
    assertThat(retrievedUser, equalTo(user));
    verify(userRepository, times(1)).findByAlternateId(eq(user.getAlternateId()), any(String.class));
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenRolesRequest_thenRolesReturned() {
    List<Role> expectedRoles = List.of(new Role(RoleName.USER));
    when(roleRepository.findAll(org.mockito.ArgumentMatchers.any(Sort.class))).thenReturn(expectedRoles);
    
    final List<Role> actualRoles = service.getRoles();
    
    assertThat(actualRoles, equalTo(expectedRoles));
    verify(roleRepository, times(1)).findAll((org.mockito.ArgumentMatchers.any(Sort.class)));
    verifyNoMoreInteractions(roleRepository);
  }
  
  @Test
  public void whenCreatingNewUserWithDuplicateUsername_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.of(new User()));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto), "error.duplicateUserName");
  }
  
  @Test
  public void whenCreatingNewUserWithDuplicateEmail_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.of(new User()));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto), "error.duplicateEmail");
  }
  
  @Test
  public void whenCreatingUserWithInvaidRole_thenNotFoundExcetpionThrown() {
    final Role role = new Role(RoleName.USER);
    final String password = "thepassword";
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(role.getName()))
        .timeZone(TimeZone.getTimeZone(ZoneId.of("Brazil/East")))
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(passwordGenerator.generate()).thenReturn(password);
    when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.save(userDto), "error.roleNotFound");
  }
  
  @Test
  public void whenCreatingUserWithValidInfo_thenUserIsCreated() {
    final Long userId = Long.valueOf(1);
    final Role role = new Role(RoleName.USER);
    final String password = "thepassword";
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(role.getName()))
        .timeZone(TimeZone.getTimeZone(ZoneId.of("Brazil/East")))
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(passwordGenerator.generate()).thenReturn(password);
    when(secretGenerator.generate()).thenReturn(CHANGE_PASSWORD);
    when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
    when(userRepository.save(any(User.class))).thenAnswer(new Answer<User>() {
      @Override
      public User answer(InvocationOnMock invocation) throws Throwable {
        final User user = (User) invocation.getArguments()[0];
        user.setId(userId);
        return user;
      }
    });
    
    final User actual = service.save(userDto);
    
    assertThat(actual.getEmail(), equalTo(userDto.getEmail()));
    assertThat(actual.getDisplayName(),
        equalTo(userDto.getFirstName() + " " + userDto.getMiddleInitial() + " " + userDto.getLastName()));
    assertThat(actual.getFirstName(), equalTo(userDto.getFirstName()));
    assertThat(actual.getId(), equalTo(userId));
    assertThat(actual.getLastName(), equalTo(userDto.getLastName()));
    assertThat(actual.getMiddleInitial(), equalTo(userDto.getMiddleInitial()));
    assertThat(actual.getPassword(), equalTo(password));
    assertThat(actual.getSecret(), equalTo(CHANGE_PASSWORD));
    assertThat(actual.getRoles(), contains(role));
    assertThat(actual.getTimeZone(), equalTo(userDto.getTimeZone()));
    assertThat(actual.getUsername(), equalTo(userDto.getUsername()));
    
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userDto.getUsername());
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verify(roleRepository, times(1)).findByName(role.getName());
    verify(userRepository, times(1)).save(any(User.class));
    verify(passwordGenerator, times(1)).generate();
    verify(secretGenerator, times(1)).generate();
    verify(passwordResetService, times(1)).initiatePasswordReset(any(User.class));
    verify(eventPublisher, times(1)).publishEvent(ArgumentMatchers.<CreatedEvent<User>>any());
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(roleRepository);
    verifyNoMoreInteractions(passwordGenerator);
    verifyNoMoreInteractions(secretGenerator);
    verifyNoMoreInteractions(passwordResetService);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  @Test
  public void whenUpdatingOwnAccount_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .changeReason(CHANGE_REASON)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .password(CHANGE_PASSWORD)
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(userDto.getId());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto), "error.cantUpdateOwnAccount");
    
    verify(securityUtils, times(1)).getPrincipal();
    verifyNoMoreInteractions(securityUtils);
  }
  
  @Test
  public void whenUpdatingUserAndCurrentUserPasswordDoesntMatch_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .changeReason(CHANGE_REASON)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .password(CHANGE_PASSWORD)
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(UUID.randomUUID());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(securityUtils.currentUserPasswordMatches(userDto.getPassword())).thenReturn(false);
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto), "error.security.passwordMismatch");
    
    verify(securityUtils, times(1)).getPrincipal();
    verify(securityUtils, times(1)).currentUserPasswordMatches(userDto.getPassword());
    verifyNoMoreInteractions(securityUtils);
  }
  
  @Test
  public void whenUpdatingUserWithDuplicateEmail_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .changeReason(CHANGE_REASON)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .password(CHANGE_PASSWORD)
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(UUID.randomUUID());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(securityUtils.currentUserPasswordMatches(userDto.getPassword())).thenReturn(true);
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.of(new User()));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto), "error.duplicateEmail");
    
    verify(securityUtils, times(1)).getPrincipal();
    verify(securityUtils, times(1)).currentUserPasswordMatches(userDto.getPassword());
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verifyNoMoreInteractions(securityUtils);
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenUpdatingUserAndUserNotFound_thenNotFoundExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .changeReason(CHANGE_REASON)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .password(CHANGE_PASSWORD)
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(UUID.randomUUID());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(securityUtils.currentUserPasswordMatches(userDto.getPassword())).thenReturn(true);
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByAlternateId(eq(userDto.getId()), any(String.class))).thenReturn(Optional.empty());
    
    assertThrows(NotFoundException.class, () -> service.save(userDto), "error.userNotFound");
    
    verify(securityUtils, times(1)).getPrincipal();
    verify(securityUtils, times(1)).currentUserPasswordMatches(userDto.getPassword());
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verify(userRepository, times(1)).findByAlternateId(any(UUID.class), any(String.class));
    verifyNoMoreInteractions(securityUtils);
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test
  public void whenUpdatingUserWithValidInfo_thenUserIsUpdated() {
    final String password = "abcdef";
    final String username = "jsmeith";
    final Role role = new Role(RoleName.USER);
    final Set<Role> roles = new HashSet<Role>();
    final Role currentRole = new Role(RoleName.ADMINISTRATOR);
    roles.add(currentRole);         //TODO need to update when > 1 role
    final User foundUser = new User();
    foundUser.setId(Long.valueOf(1));
    foundUser.setAccountEnabled(true);
    foundUser.setEmail("abc@123.com");
    foundUser.setFirstName("Joe");
    foundUser.setLastName("Smith");
    foundUser.setMiddleInitial("M");
    foundUser.setPassword(password);
    foundUser.setSecret("aaa");
    foundUser.setRoles(roles);
    foundUser.setTimeZone(TimeZone.getDefault());
    foundUser.setUsername(username);
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .changeReason(CHANGE_REASON)
        .email("a" + foundUser.getEmail())
        .firstName("a" + foundUser.getFirstName())
        .lastName("a" + foundUser.getLastName())
        .middleInitial(foundUser.getMiddleInitial() != null ? null : "M")
        .roles(List.of(role.getName()))
        .timeZone(TimeZone.getDefault())
        .username(username)
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(UUID.randomUUID());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(securityUtils.currentUserPasswordMatches(userDto.getPassword())).thenReturn(true);
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByAlternateId(eq(userDto.getId()),
        org.mockito.ArgumentMatchers.any(String.class))).thenReturn(Optional.of(foundUser));
    when(roleRepository.findByName(role.getName())).thenReturn(Optional.of(role));
    when(userRepository.save(any(User.class))).thenAnswer(new Answer<User>() {
      @Override
      public User answer(InvocationOnMock invocation) throws Throwable {
        final User user = (User) invocation.getArguments()[0];
        return user;
      }
    });
    
    final User actual = service.save(userDto);
    
    assertThat(actual.getEmail(), equalTo(userDto.getEmail()));
    assertThat(actual.getChangeReason(),equalTo(userDto.getChangeReason()));
    assertThat(actual.getDisplayName(),
        equalTo(userDto.getFirstName() + " " + userDto.getLastName()));
    assertThat(actual.getFirstName(), equalTo(userDto.getFirstName()));
    assertThat(actual.getId(), equalTo(foundUser.getId()));
    assertThat(actual.getLastName(), equalTo(userDto.getLastName()));
    assertThat(actual.getMiddleInitial(), equalTo(userDto.getMiddleInitial()));
    assertThat(actual.getPassword(), equalTo(password));
    assertThat(actual.getSecret(), equalTo("aaa"));
    assertThat(actual.getRoles().size(), equalTo(1));
    assertThat(actual.getRoles(), contains(role));
    assertThat(actual.getTimeZone(), equalTo(userDto.getTimeZone()));
    assertThat(actual.getUsername(), equalTo(username));
    
    verify(securityUtils, times(1)).getPrincipal();
    verify(securityUtils, times(1)).currentUserPasswordMatches(userDto.getPassword());
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verify(userRepository, times(1)).findByAlternateId(eq(userDto.getId()), any(String.class));
    verify(roleRepository, times(1)).findByName(role.getName());
    verify(userRepository, times(1)).save(any(User.class));
    verify(eventPublisher, times(1)).publishEvent(ArgumentMatchers.<UpdatedEvent<User>>any());
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(roleRepository);
    verifyNoMoreInteractions(passwordGenerator);
    verifyNoMoreInteractions(eventPublisher);
    verifyNoMoreInteractions(securityUtils);
    verifyNoInteractions(secretGenerator);
  }
  
  @Test
  public void whenDisableUnusedAccountsCalled_thenDisableUnusedAccountOnRepositoryCalled() {
    this.service.setDisableUnusedAccountsGreaterThanMonths(Long.valueOf(12));
    this.service.disableUnusedAccounts();
    
    verify(userRepository, times(1)).disableUnusedAccounts(any(LocalDateTime.class));
    verifyNoMoreInteractions(userRepository);
  }
}
