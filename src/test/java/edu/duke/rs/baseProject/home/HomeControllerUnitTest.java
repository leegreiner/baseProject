package edu.duke.rs.baseProject.home;

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
import edu.duke.rs.baseProject.login.LoginController;


@RunWith(SpringRunner.class)
@WebMvcTest(HomeController.class)
public class HomeControllerUnitTest extends BaseWebTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	public void whenNotAuthenticated_thenRedirectToLogin() throws Exception {
		this.mockMvc.perform(get(HomeController.HOME_MAPPING))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
	}
	
	@Test
	@WithMockUser(username = "test")
	public void whenAuthenticated_thenHomeReturned() throws Exception {
		this.mockMvc.perform(get(HomeController.HOME_MAPPING))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name(HomeController.HOME_VIEW));
	}
}
