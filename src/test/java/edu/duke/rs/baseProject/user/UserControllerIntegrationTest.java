package edu.duke.rs.baseProject.user;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.RoleName;

public class UserControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Test
  public void whenNotAuthenticated_thenUnauthorizedErrorReturned() throws Exception {
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
}
