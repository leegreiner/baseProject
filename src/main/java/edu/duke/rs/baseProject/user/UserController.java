package edu.duke.rs.baseProject.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.duke.rs.baseProject.BaseWebController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController extends BaseWebController {
	public static final String USERS_VIEW =  "/users/users";
	public static final String USERS_MAPPING = "/users";
	private transient final UserService userService;
	
	public UserController(final UserService userService) {
		this.userService = userService;
	}
	
	@GetMapping(USERS_MAPPING)
	public String getUsers() {
	  log.debug("In getUsers()");
		return USERS_VIEW;
	}
}
