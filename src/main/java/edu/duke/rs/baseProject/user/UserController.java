package edu.duke.rs.baseProject.user;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {
	public static final String USERS_VIEW = "users/users";
	
	@GetMapping("/user")
	public String getUsers() {
		return USERS_VIEW;
	}
}
