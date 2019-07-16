package edu.duke.rs.baseProject.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;

public class IndexControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Test
  public void whenNotAuthenticated_thenIndexReturned() throws Exception {
    this.mockMvc.perform(get(IndexController.INDEX_MAPPING))
      .andExpect(status().isOk());
  }
}