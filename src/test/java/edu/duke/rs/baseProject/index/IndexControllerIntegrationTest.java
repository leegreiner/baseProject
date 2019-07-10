package edu.duke.rs.baseProject.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.duke.rs.baseProject.BaseWebTest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class IndexControllerIntegrationTest extends BaseWebTest {
  @Autowired
  private MockMvc mockMvc;

  @Test
  public void whenNotAuthenticated_thenIndexReturned() throws Exception {
    this.mockMvc.perform(get(IndexController.INDEX_MAPPING))
      .andExpect(status().isOk());
  }
}
