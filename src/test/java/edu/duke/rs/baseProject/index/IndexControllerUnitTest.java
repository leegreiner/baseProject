package edu.duke.rs.baseProject.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.home.HomeController;

@WebMvcTest(IndexController.class)
public class IndexControllerUnitTest extends AbstractWebUnitTest {
  @Test
  public void whenNotAuthenticated_thenIndexDisplayed() throws Exception {
    this.mockMvc.perform(get(IndexController.INDEX_MAPPING)).andExpect(status().isOk())
        .andExpect(view().name(IndexController.INDEX_VIEW));
  }

  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenAuthenticated_thenHomeDisplayed() throws Exception {
    this.mockMvc.perform(get(IndexController.INDEX_MAPPING)).andExpect(status().isFound())
        .andExpect(redirectedUrl(HomeController.HOME_MAPPING));
  }
}
