package edu.duke.rs.baseProject.home;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.RoleName;

@WebMvcTest(HomeController.class)
public class HomeControllerUnitTest extends AbstractWebUnitTest {
  private UserDetailsBuilder userDetailsBuilder = new UserDetailsBuilder();
  
	@Test
	public void whenNotAuthenticated_thenRedirectToLogin() throws Exception {
		this.mockMvc.perform(get(HomeController.HOME_MAPPING))
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
	}
	
	@Test
	public void whenAuthenticated_thenHomeReturned() throws Exception {
		this.mockMvc.perform(get(HomeController.HOME_MAPPING)
		   .with(user(userDetailsBuilder.build(Long.valueOf(1), "abc@123.com", Set.of(RoleName.USER)))))
			.andExpect(status().isOk())
			.andExpect(view().name(HomeController.HOME_VIEW));
	}
}
