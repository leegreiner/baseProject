package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;

public class UserControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  
  @Test
  public void whenNotAuthenticated_thenUsersRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)).andExpect(status().isFound())
        .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAuthenticated_thenUsersReturned() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USERS_VIEW));
  }
  
  @Test
  public void whenNotAuthenticated_thenUserDetailsRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USER_DETAILS_MAPPING, 1L))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAuthenticated_thenUserDetailsReturned() throws Exception { 
    final Role role = roleRepository.save(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    User user = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    user = userRepository.save(user);
    
    this.mockMvc.perform(get(UserController.USER_DETAILS_MAPPING, user.getId())
      .with(user(UserDetailsBuilder.build(user.getId(), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USER_DETAILS_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, equalTo(user)));
  }
}
