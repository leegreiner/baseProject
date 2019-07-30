package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;

public class UserProfileControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  
  @Test
  public void whenNotAuthenticated_thenLoginPageReturned() throws Exception {
    this.mockMvc.perform(get(UserProfileController.USER_PROFILE_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAuthenticated_thenUserProfileReturned() throws Exception { 
    final Role role = roleRepository.save(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    User user1 = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    user1 = userRepository.save(user1);
    
    this.mockMvc.perform(get(UserProfileController.USER_PROFILE_MAPPING)
      .with(user(UserDetailsBuilder.build(user1.getId(), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserProfileController.USER_PROFILE_VIEW))
      .andExpect(model().attribute(UserProfileController.USER_PROFILE_ATTRIBUTE, notNullValue()));
  }
  
  @Test
  public void whenUserProfilePassed_thenUpdateUserProfileReturnsToHomeView() throws Exception {
    final Role role = roleRepository.save(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    User user1 = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    user1 = userRepository.save(user1);
    
    final UserDetails appUser = UserDetailsBuilder.build(user1.getId(), RoleName.USER);
    
    this.mockMvc.perform(put(UserProfileController.USER_PROFILE_MAPPING).with(csrf()).param("timeZone", "GMT")
        .with(user(appUser)))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(HomeController.HOME_MAPPING))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, "Your profile has been updated."));
    
    assertThat(TimeZone.getTimeZone("GMT"), equalTo(userRepository.findById(user1.getId()).get().getTimeZone()));
    
  }
}
