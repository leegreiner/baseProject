package edu.duke.rs.baseProject.home;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.login.LoginController;

@WebMvcTest(HomeController.class)
public class HomeControllerUnitTest extends AbstractWebUnitTest {
	@Test
	public void whenNotAuthenticated_thenRedirectToLogin() throws Exception {
		this.mockMvc.perform(get(HomeController.HOME_MAPPING))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
	}
	
	@Test
	@WithMockUser(username = "test", authorities = {"USER"})
	public void whenAuthenticated_thenHomeReturned() throws Exception {
		this.mockMvc.perform(get(HomeController.HOME_MAPPING))
			.andExpect(status().isOk())
			.andExpect(view().name(HomeController.HOME_VIEW));
	}
}
