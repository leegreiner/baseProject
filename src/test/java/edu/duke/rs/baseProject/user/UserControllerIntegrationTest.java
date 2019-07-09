package edu.duke.rs.baseProject.user;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import edu.duke.rs.baseProject.BaseWebTest;
import edu.duke.rs.baseProject.login.LoginController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public class UserControllerIntegrationTest extends BaseWebTest {
	@Autowired
    private MockMvc mockMvc;
	
	@Test
	public void whenNotAuthenticated_thenUnauthorizedErrorReturned() throws Exception {
		this.mockMvc.perform(get(UserController.USERS_MAPPING))
		.andExpect(status().isFound())
		.andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
	}
}
