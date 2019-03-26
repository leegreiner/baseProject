package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.duke.rs.baseProject.BaseWebTest;
import edu.duke.rs.baseProject.login.LoginController;


@RunWith(SpringRunner.class)
@WebMvcTest(UserController.class)
public class UserControllerUnitTest extends BaseWebTest {
	@Autowired
    private MockMvc mockMvc;
	@MockBean
	private UserService userService;
	
	@Test
	public void whenNotAuthenticated_thenRedirectToLogin() throws Exception {
		assertThat(this.userService, notNullValue());
		
		this.mockMvc.perform(get(UserController.USERS_MAPPING))
			.andDo(print())
			.andExpect(status().isFound())
			.andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
	}
	
	@Test
	@WithMockUser(username = "test")
	public void whenAuthenticated_thenUsersReturned() throws Exception {
		this.mockMvc.perform(get(UserController.USERS_MAPPING))
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(view().name(UserController.USERS_VIEW));
	}
}
