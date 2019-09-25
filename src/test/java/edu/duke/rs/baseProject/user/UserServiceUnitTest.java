package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.time.ZoneId;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;

import edu.duke.rs.baseProject.event.CreatedEvent;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.security.password.PasswordGenerator;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetService;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*"})
@PrepareForTest(SecurityUtils.class)
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
  
  private UserServiceImpl service;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    mockStatic(SecurityUtils.class);
    service = new UserServiceImpl(userRepository, roleRepository, passwordGenerator, passwordResetService, eventPublisher);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void whenNoCurrentUser_thenGetUserProfileThrowsIllegalArgumentExceptionThrown() {
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    service.getUserProfile();
  }
  
  @Test
  public void whenUserNotFound_thenGetUserProfileThrowsNotFoundException() {
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
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
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.of(user));
    
    final UserProfile userProfile = service.getUserProfile();
    
    assertThat(userProfile.getTimeZone(), equalTo(user.getTimeZone()));
    verify(userRepository, times(1)).findById(appPrincipal.getUserId());
    verifyNoMoreInteractions(userRepository);
    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void whenNoCurrentUser_thenUpdateUserProfileThrowsIllegalArgumentExceptionThrown() {
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.empty());
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("GMT"));
    
    service.updateUserProfile(userProfile);
  }
  
  @Test
  public void whenUserNotFound_thenUpdateUserProfileThrowsNotFoundException() {
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
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
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.of(user));
    
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("GMT"));
    
    service.updateUserProfile(userProfile);
    
    assertThat(user.getTimeZone(), equalTo(userProfile.getTimeZone()));
    verify(userRepository, times(1)).findById(appPrincipal.getUserId());
    verifyNoMoreInteractions(userRepository);
  }
  
  @Test(expected = NotFoundException.class)
  public void whenUserNotFound_thenGetUserThrowsNotFoundException() {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUserName("abc");
    when(userRepository.findById(user.getId())).thenReturn(Optional.empty());
    
    service.getUser(user.getId());
  }
  
  @Test
  public void whenUserFound_thenGetUserReturnsUser() {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUserName("abc");
    when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
    
    final User retrievedUser = service.getUser(user.getId());
    
    assertThat(retrievedUser, equalTo(user));
    verify(userRepository, times(1)).findById(user.getId());
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
  
  @Test(expected = ConstraintViolationException.class)
  public void whenCreatingNewUserWithDuplicateUsername_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .userName("jsmith")
        .build();

    when(userRepository.findByUserNameIgnoreCase(userDto.getUserName())).thenReturn(Optional.of(new User()));
    
    service.save(userDto);
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void whenCreatingNewUserWithDuplicateEmail_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .userName("jsmith")
        .build();

    when(userRepository.findByUserNameIgnoreCase(userDto.getUserName())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.of(new User()));
    
    service.save(userDto);
  }
  
  @Test(expected = NotFoundException.class)
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
        .userName("jsmith")
        .build();

    when(userRepository.findByUserNameIgnoreCase(userDto.getUserName())).thenReturn(Optional.empty());
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(passwordGenerator.generate()).thenReturn(password);
    when(roleRepository.findByName(role.getName())).thenReturn(Optional.empty());

    service.save(userDto);
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
        .userName("jsmith")
        .build();

    when(userRepository.findByUserNameIgnoreCase(userDto.getUserName())).thenReturn(Optional.empty());
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
    assertThat(actual.getUserName(), equalTo(userDto.getUserName()));
    
    verify(userRepository, times(1)).findByUserNameIgnoreCase(userDto.getUserName());
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verify(roleRepository, times(1)).findByName(role.getName());
    verify(userRepository, times(1)).save(any(User.class));
    verify(passwordGenerator, times(1)).generate();
    verify(passwordResetService, times(1)).initiatePasswordReset(any(User.class));
    verify(eventPublisher, times(1)).publishEvent(any(CreatedEvent.class));
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(roleRepository);
    verifyNoMoreInteractions(passwordGenerator);
    verifyNoMoreInteractions(passwordResetService);
    verifyNoMoreInteractions(eventPublisher);
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void whenUpdatingOwnAccount_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .userName("jsmith")
        .id(Long.valueOf(1))
        .build();
    userDto.setId(Long.valueOf(1));

    when(appPrincipal.getUserId()).thenReturn(userDto.getId());
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    service.save(userDto);
  }
  
  @Test(expected = ConstraintViolationException.class)
  public void whenUpdatingUserWithDuplicateEmail_thenConstraintViolationExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .userName("jsmith")
        .id(Long.valueOf(1))
        .build();
    final User foundUser = new User();
    foundUser.setId(userDto.getId() + 1);

    when(appPrincipal.getUserId()).thenReturn(userDto.getId() + 1);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.of(foundUser));
    
    service.save(userDto);
  }
  
  @Test(expected = NotFoundException.class)
  public void whenUpdatingUserAndUserNotFound_thenNotFoundExceptionThrown() {
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getDefault())
        .userName("jsmith")
        .id(Long.valueOf(1))
        .build();
    final User foundUser = new User();
    foundUser.setId(userDto.getId() + 1);

    when(appPrincipal.getUserId()).thenReturn(userDto.getId() + 1);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());
    
    service.save(userDto);
  }
  
  @Test
  public void whenUpdatingUserWithValidInfo_thenUserIsUpdated() {
    final String password = "abcdef";
    final String userName = "jsmeith";
    final Role role = new Role(RoleName.USER);
    final UserDto userDto = UserDto.builder()
        .accountEnabled(true)
        .email("abc@123.com")
        .firstName("Joe")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(role.getName().name()))
        .timeZone(TimeZone.getDefault())
        .userName("jsmith")
        .id(Long.valueOf(1))
        .build();
    final Set<Role> roles = new HashSet<Role>();
    final Role currentRole = new Role(RoleName.ADMINISTRATOR);
    roles.add(currentRole);         //TODO need to update when > 1 role
    final User foundUser = new User();
    foundUser.setId(userDto.getId());
    foundUser.setAccountEnabled(! userDto.isAccountEnabled());
    foundUser.setEmail("a" + userDto.getEmail());
    foundUser.setFirstName("a" + userDto.getFirstName());
    foundUser.setLastName("a" + userDto.getLastName());
    foundUser.setMiddleInitial(userDto.getMiddleInitial() != null ? null : "M");
    foundUser.setPassword(password);
    foundUser.setRoles(roles);
    foundUser.setTimeZone(TimeZone.getDefault());
    foundUser.setUserName(userName);

    when(appPrincipal.getUserId()).thenReturn(userDto.getId() + 1);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(userRepository.findByEmailIgnoreCase(userDto.getEmail())).thenReturn(Optional.empty());
    when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(foundUser));
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
        equalTo(userDto.getFirstName() + " " + userDto.getMiddleInitial() + " " + userDto.getLastName()));
    assertThat(actual.getFirstName(), equalTo(userDto.getFirstName()));
    assertThat(actual.getId(), equalTo(foundUser.getId()));
    assertThat(actual.getLastName(), equalTo(userDto.getLastName()));
    assertThat(actual.getMiddleInitial(), equalTo(userDto.getMiddleInitial()));
    assertThat(actual.getPassword(), equalTo(password));
    assertThat(actual.getRoles().size(), equalTo(1));
    assertThat(actual.getRoles(), contains(role));
    assertThat(actual.getTimeZone(), equalTo(userDto.getTimeZone()));
    assertThat(actual.getUserName(), equalTo(userName));
    
    verify(userRepository, times(1)).findByEmailIgnoreCase(userDto.getEmail());
    verify(userRepository, times(1)).findById(userDto.getId());
    verify(roleRepository, times(1)).findByName(role.getName());
    verify(userRepository, times(1)).save(any(User.class));
    verifyNoMoreInteractions(userRepository);
    verifyNoMoreInteractions(roleRepository);
    verifyNoMoreInteractions(passwordGenerator);
    verifyNoMoreInteractions(eventPublisher);
  }
}
