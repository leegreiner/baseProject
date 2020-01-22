package edu.duke.rs.baseProject.login;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.security.SecurityUtils;

@WebMvcTest(LoginController.class)
public class LoginControllerUnitTest extends AbstractWebUnitTest {
  @MockBean
  private SecurityUtils securityUtils;
  
  @Test
  public void whenNotAuthenticated_thenLoginDisplayed() throws Exception {
    when(securityUtils.userIsAuthenticated()).thenReturn(false);
    
    this.mockMvc.perform(get(LoginController.LOGIN_MAPPING))
      .andExpect(status().isOk())
      .andExpect(view().name(LoginController.LOGIN_VIEW));
  }

  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenAuthenticated_thenHomeReturned() throws Exception {
    when(securityUtils.userIsAuthenticated()).thenReturn(true);
    
    this.mockMvc.perform(get(LoginController.LOGIN_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(HomeController.HOME_MAPPING));
  }
}
