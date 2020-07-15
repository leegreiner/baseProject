package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
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

import edu.duke.rs.baseProject.event.CreatedEvent;
import edu.duke.rs.baseProject.event.UpdatedEvent;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.security.password.PasswordGenerator;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetService;



public class UserServiceUnitTest {
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private UserRepository userRepository;
  @Mock
  private RoleRepository roleRepository;
  @Mock
  private PasswordGenerator passwordGenerator;
  @Mock
  private PasswordResetService passwordResetService;
  @Mock
  private ApplicationEventPublisher eventPublisher;
  @Mock
  private SecurityUtils securityUtils;
  
  private UserServiceImpl service;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    service = new UserServiceImpl(userRepository, roleRepository, passwordGenerator, passwordResetService, eventPublisher, securityUtils);
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
    
    assertThrows(IllegalArgumentException.class, () -> service.updateUserProfile(userProfile));
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
    
    assertThrows(NotFoundException.class, () -> service.getUser(user.getAlternateId()));
  }
  
  @Test
  public void whenUserFound_thenGetUserReturnsUser() {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUsername("abc");
    when(userRepository.findByAlternateId(user.getAlternateId())).thenReturn(Optional.of(user));
    
    final User retrievedUser = service.getUser(user.getAlternateId());
    
    assertThat(retrievedUser, equalTo(user));
    verify(userRepository, times(1)).findByAlternateId(user.getAlternateId());
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
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.of(new User()));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto));
  }
  
  @Test
  public void whenCreatingNewUserWithDuplicateEmail_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.of(new User()));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto));
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
        .roles(List.of(role.getName().name()))
        .timeZone(TimeZone.getTimeZone(ZoneId.of("Brazil/East")))
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(passwordGenerator.generate()).thenReturn(password);
    when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

    assertThrows(NotFoundException.class, () -> service.save(userDto));
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
        .roles(List.of(role.getName().name()))
        .timeZone(TimeZone.getTimeZone(ZoneId.of("Brazil/East")))
        .username("jsmith")
        .build();

    when(userRepository.findByUsernameIgnoreCase(userDto.getUsername())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(passwordGenerator.generate()).thenReturn(password);
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
    assertThat(actual.getRoles(), contains(role));
    assertThat(actual.getTimeZone(), equalTo(userDto.getTimeZone()));
    assertThat(actual.getUsername(), equalTo(userDto.getUsername()));
    
    verify(userRepository, times(1)).findByUsernameIgnoreCase(userDto.getUsername());
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verify(roleRepository, times(1)).findByName(role.getName());
    verify(userRepository, times(1)).save(any(User.class));
    verify(passwordGenerator, times(1)).generate();
    verify(passwordResetService, times(1)).initiatePasswordReset(any(User.class));
    verify(eventPublisher, times(1)).publishEvent(ArgumentMatchers.<CreatedEvent<User>>any());
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(roleRepository);
    verifyNoMoreInteractions(passwordGenerator);
    verifyNoMoreInteractions(passwordResetService);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  @Test
  public void whenUpdatingOwnAccount_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(userDto.getId());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto));
  }
  
  @Test
  public void whenUpdatingUserWithDuplicateEmail_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(UUID.randomUUID());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.of(new User()));
    
    assertThrows(ConstraintViolationException.class, () -> service.save(userDto));
  }
  
  @Test
  public void whenUpdatingUserAndUserNotFound_thenNotFoundExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .username("jsmith")
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(UUID.randomUUID());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByAlternateId(userDto.getId())).thenReturn(Optional.empty());
    
    assertThrows(NotFoundException.class, () -> service.save(userDto));
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
    foundUser.setRoles(roles);
    foundUser.setTimeZone(TimeZone.getDefault());
    foundUser.setUsername(username);
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("a" + foundUser.getEmail())
        .firstName("a" + foundUser.getFirstName())
        .lastName("a" + foundUser.getLastName())
        .middleInitial(foundUser.getMiddleInitial() != null ? null : "M")
        .roles(List.of(role.getName().name()))
        .timeZone(TimeZone.getDefault())
        .username(username)
        .id(UUID.randomUUID())
        .build();

    when(appPrincipal.getAlternateUserId()).thenReturn(UUID.randomUUID());
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findByAlternateId(userDto.getId())).thenReturn(Optional.of(foundUser));
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
    assertThat(actual.getDisplayName(),
        equalTo(userDto.getFirstName() + " " + userDto.getLastName()));
    assertThat(actual.getFirstName(), equalTo(userDto.getFirstName()));
    assertThat(actual.getId(), equalTo(foundUser.getId()));
    assertThat(actual.getLastName(), equalTo(userDto.getLastName()));
    assertThat(actual.getMiddleInitial(), equalTo(userDto.getMiddleInitial()));
    assertThat(actual.getPassword(), equalTo(password));
    assertThat(actual.getRoles().size(), equalTo(1));
    assertThat(actual.getRoles(), contains(role));
    assertThat(actual.getTimeZone(), equalTo(userDto.getTimeZone()));
    assertThat(actual.getUsername(), equalTo(username));
    
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verify(userRepository, times(1)).findByAlternateId(userDto.getId());
    verify(roleRepository, times(1)).findByName(role.getName());
    verify(userRepository, times(1)).save(any(User.class));
    verify(eventPublisher, times(1)).publishEvent(ArgumentMatchers.<UpdatedEvent<User>>any());
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(roleRepository);
    verifyNoMoreInteractions(passwordGenerator);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  @Test
  public void whenDisableUnusedAccountsCalled_thenDisableUnusedAccountOnRepositoryCalled() {
    this.service.setDisableUnusedAccountsGreaterThanMonths(Long.valueOf(12));
    this.service.disableUnusedAccounts();
    
    verify(userRepository, times(1)).disableUnusedAccounts(any(LocalDateTime.class));
    verifyNoMoreInteractions(userRepository);
  }
}
