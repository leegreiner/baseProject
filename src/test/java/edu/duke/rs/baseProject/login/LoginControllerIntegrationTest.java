package edu.duke.rs.baseProject.login;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.duke.rs.baseProject.BaseWebTest;
import edu.duke.rs.baseProject.home.HomeController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class LoginControllerIntegrationTest extends BaseWebTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void whenNotAuthenticated_thenIndexReturned() throws Exception {
    this.mockMvc.perform(get(LoginController.LOGIN_MAPPING))
      .andExpect(status().isOk())
      .andExpect(view().name(LoginController.LOGIN_VIEW));
  }

  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenAuthenticated_thenHomeReturned() throws Exception {
    this.mockMvc.perform(get(LoginController.LOGIN_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(HomeController.HOME_MAPPING));
  }
}
