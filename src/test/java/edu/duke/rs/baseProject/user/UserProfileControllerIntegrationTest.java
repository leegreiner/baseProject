package edu.duke.rs.baseProject.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.login.LoginController;

public class UserProfileControllerIntegrationTest extends AbstractWebIntegrationTest {
  
  @Test
  public void whenNotAuthenticated_thenLoginPageReturned() throws Exception {
    this.mockMvc.perform(get(UserProfileController.USER_PROFILE_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }

}
