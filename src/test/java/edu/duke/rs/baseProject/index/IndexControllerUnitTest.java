package edu.duke.rs.baseProject.index;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.duke.rs.baseProject.BaseWebTest;
import edu.duke.rs.baseProject.home.HomeController;


@RunWith(SpringRunner.class)
@WebMvcTest(IndexController.class)
public class IndexControllerUnitTest extends BaseWebTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	public void whenNotAuthenticated_thenIndexDisplayed() throws Exception {
		this.mockMvc.perform(get(IndexController.INDEX_MAPPING))
		.andDo(print())
		.andExpect(status().isOk())
		.andExpect(view().name(IndexController.INDEX_VIEW));
	}
	
	@Test
	@WithMockUser(username = "test", authorities = {"USER"})
	public void whenAuthenticated_thenHomeDisplayed() throws Exception {
		this.mockMvc.perform(get(IndexController.INDEX_MAPPING))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(HomeController.HOME_MAPPING));
	}
}

