package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Optional;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
public class UserServiceUnitTest {
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private UserRepository userRepository;
 
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    mockStatic(SecurityUtils.class);
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void whenNoCurrentUser_thenGetUserProfileThrowsIllegalArgumentExceptionThrown() {
    when(SecurityUtils.getPrincipal()).thenReturn(null);
    
    final UserServiceImpl service = new UserServiceImpl(userRepository);
    
    service.getUserProfile();
  }
  
  @Test
  public void whenUserNotFound_thenGetUserProfileThrowsNotFoundException() {
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(SecurityUtils.getPrincipal()).thenReturn(appPrincipal);
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.empty());
    
    final UserServiceImpl service = new UserServiceImpl(userRepository);
    
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
    when(SecurityUtils.getPrincipal()).thenReturn(appPrincipal);
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.of(user));
    
    final UserServiceImpl service = new UserServiceImpl(userRepository);
    final UserProfile userProfile = service.getUserProfile();
    
    assertThat(userProfile.getTimeZone(), equalTo(user.getTimeZone()));
    verify(userRepository, times(1)).findById(appPrincipal.getUserId());
    verifyNoMoreInteractions(userRepository);
    
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void whenNoCurrentUser_thenUpdateUserProfileThrowsIllegalArgumentExceptionThrown() {
    when(SecurityUtils.getPrincipal()).thenReturn(null);
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("GMT"));
    final UserServiceImpl service = new UserServiceImpl(userRepository);
    
    service.updateUserProfile(userProfile);
  }
  
  @Test
  public void whenUserNotFound_thenUpdateUserProfileThrowsNotFoundException() {
    when(appPrincipal.getUserId()).thenReturn(1L);
    when(SecurityUtils.getPrincipal()).thenReturn(appPrincipal);
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.empty());
    
    final UserServiceImpl service = new UserServiceImpl(userRepository);
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
    when(SecurityUtils.getPrincipal()).thenReturn(appPrincipal);
    when(userRepository.findById(appPrincipal.getUserId())).thenReturn(Optional.of(user));
    
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("GMT"));
    final UserServiceImpl service = new UserServiceImpl(userRepository);
    
    service.updateUserProfile(userProfile);
    
    assertThat(user.getTimeZone(), equalTo(userProfile.getTimeZone()));
    verify(userRepository, times(1)).findById(appPrincipal.getUserId());
    verifyNoMoreInteractions(userRepository);
    
  }
  
}
