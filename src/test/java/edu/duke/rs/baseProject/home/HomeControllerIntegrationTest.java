package edu.duke.rs.baseProject.home;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.login.LoginController;

public class HomeControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Test
  public void whenNotAuthenticated_thenLoginPageReturned() throws Exception {
    this.mockMvc.perform(get(HomeController.HOME_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
}
