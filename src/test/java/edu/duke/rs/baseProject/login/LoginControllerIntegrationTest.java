package edu.duke.rs.baseProject.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.security.test.context.support.WithMockUser;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.home.HomeController;

public class LoginControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Test
  public void whenNotAuthenticated_thenIndexReturned() throws Exception {
    this.mockMvc.perform(get(LoginController.LOGIN_MAPPING))
      .andExpect(status().isOk())
      .andExpect(view().name(LoginController.LOGIN_VIEW));
  }

  @Test
  @WithMockUser(username = "test", authorities = { "VIEW_USERS" })
  public void whenAuthenticated_thenHomeReturned() throws Exception {
    this.mockMvc.perform(get(LoginController.LOGIN_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(HomeController.HOME_MAPPING));
  }
}
