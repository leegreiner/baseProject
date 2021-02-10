package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

@WebMvcTest(UserProfileController.class)
public class UserProfileControllerUnitTest extends AbstractWebUnitTest{
  private static final String EMAIL = "abc@123.com";
  @MockBean
  private UserService userService;
  @MockBean
  private BindingResult bindingResult;
  @MockBean
  private SecurityUtils securityUtils;
  private UserDetailsBuilder userDetailsBuilder = new UserDetailsBuilder();
  
  @Test
  public void whenNotAuthenticated_thenRedirectToLogin() throws Exception {
    when(securityUtils.userIsAuthenticated()).thenReturn(false);
    
    this.mockMvc.perform(get(UserProfileController.USER_PROFILE_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAuthenticated_thenUserProfileReturned() throws Exception {
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("UTC"));
    when(securityUtils.userIsAuthenticated()).thenReturn(true);
    when(userService.getUserProfile()).thenReturn(userProfile);
    
    this.mockMvc.perform(get(UserProfileController.USER_PROFILE_MAPPING)
       .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL, Set.of(RoleName.USER)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserProfileController.USER_PROFILE_VIEW))
      .andExpect(model().attribute(UserProfileController.USER_PROFILE_ATTRIBUTE, equalTo(userProfile)));
    
    verify(userService, times(1)).getUserProfile();
    verifyNoMoreInteractions(userService);
  }
  
  @Test
  public void whenBindingResultHasErrors_thenUpdateUserProfileReturnsUserProfileView() throws Exception{
    final UserProfile userProfile = new UserProfile(TimeZone.getTimeZone("UTC"));
    
    when(securityUtils.userIsAuthenticated()).thenReturn(true);
    when(userService.getUserProfile()).thenReturn(userProfile);
    
    this.mockMvc.perform(put(UserProfileController.USER_PROFILE_MAPPING).with(csrf()).param("timeZone", "abc")
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL, Set.of(RoleName.USER)))))
        .andExpect(view().name(UserProfileController.USER_PROFILE_VIEW))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
    
    verifyNoMoreInteractions(securityUtils);
    verifyNoMoreInteractions(userService);
  }
  
  @Test
  public void whenBadUserProfilePassed_thenErrorMessageReturned() throws Exception {
    doThrow(new IllegalArgumentException("unknownError")).when(userService).updateUserProfile(any(UserProfile.class));
    final Set<Role> roles = new HashSet<Role>();
    final User user = new User();
    user.setRoles(roles);
    
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(new AppPrincipal(user, false, false)));
    
    this.mockMvc.perform(put(UserProfileController.USER_PROFILE_MAPPING).with(csrf()).param("timeZone", "GMT")
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL, Set.of(RoleName.USER)))))
        .andExpect(view().name(UserProfileController.USER_PROFILE_VIEW))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("An unknown error has occurred.")));
    
    verify(securityUtils, times(1)).getPrincipal();
    verify(userService, times(1)).updateUserProfile(any(UserProfile.class));
    verifyNoMoreInteractions(securityUtils);
    verifyNoMoreInteractions(userService);
  }
  
  @Test
  public void whenUserProfilePassed_thenUpdateUserProfileReturnsToHomeView() throws Exception {
    final UserDetails appUser = userDetailsBuilder.build(Long.valueOf(1), EMAIL, Set.of(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    final User user = new User();
    user.setRoles(roles);
    
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(new AppPrincipal(user, false, false)));
    
    this.mockMvc.perform(put(UserProfileController.USER_PROFILE_MAPPING).with(csrf()).param("timeZone", "GMT")
        .with(user(appUser)))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(HomeController.HOME_MAPPING))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, "Your profile has been updated."));
    
    verify(securityUtils, times(1)).getPrincipal();
    verify(userService, times(1)).updateUserProfile(any(UserProfile.class));
    verifyNoMoreInteractions(securityUtils);
    verifyNoMoreInteractions(userService);
  }
}
