package edu.duke.rs.baseProject.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.security.test.context.support.WithMockUser;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.login.LoginController;

public class UserControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Test
  public void whenNotAuthenticated_thenUnauthorizedErrorReturned() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)).andExpect(status().isFound())
        .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenAuthenticated_thenUsersReturned() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USERS_VIEW));
  }
}
