package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Set;
import java.util.TimeZone;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.PersistentUserDetailsBuilder;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AppPrincipal;

public class UserProfileControllerIntegrationTest extends AbstractWebIntegrationTest {
  private static final String EMAIL = "abc@123.com";
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PersistentUserDetailsBuilder persistentUserDetailsBuilder;

  @Test
  public void whenNotAuthenticated_thenLoginPageReturned() throws Exception {
    this.mockMvc.perform(get(UserProfileController.USER_PROFILE_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAuthenticated_thenUserProfileReturned() throws Exception {   
    this.mockMvc.perform(get(UserProfileController.USER_PROFILE_MAPPING)
      .with(user(persistentUserDetailsBuilder.build(EMAIL, Set.of(RoleName.USER)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserProfileController.USER_PROFILE_VIEW))
      .andExpect(model().attribute(UserProfileController.USER_PROFILE_ATTRIBUTE, notNullValue()));
  }
  
  @Test
  public void whenUserProfilePassed_thenUpdateUserProfileReturnsToHomeView() throws Exception {
    final AppPrincipal appPrincipal = (AppPrincipal) persistentUserDetailsBuilder.build(EMAIL, Set.of(RoleName.USER));
    
    this.mockMvc.perform(put(UserProfileController.USER_PROFILE_MAPPING)
        .with(user(appPrincipal))
        .with(csrf())
        .param("timeZone", "GMT"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(HomeController.HOME_MAPPING))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, "Your profile has been updated."));
    
    assertThat(TimeZone.getTimeZone("GMT"), equalTo(userRepository.findById(appPrincipal.getUserId()).get().getTimeZone()));
  }
}
