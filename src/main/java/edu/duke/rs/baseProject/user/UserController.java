package edu.duke.rs.baseProject.user;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import edu.duke.rs.baseProject.BaseWebController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController extends BaseWebController {
	public static final String USERS_VIEW =  "/users/users";
	public static final String USER_DETAILS_VIEW = "/users/userDetails";
	public static final String USERS_MAPPING = "/users";
	public static final String USER_DETAILS_MAPPING = USERS_MAPPING + "/{userId}";
	public static final String USER_MODEL_ATTRIBUTE = "user";
	private transient final UserService userService;
	
	public UserController(final UserService userService) {
	  this.userService = userService;
	}
	
	@GetMapping(USERS_MAPPING)
	public String getUsers() {
	  log.debug("In getUsers()");
		return USERS_VIEW;
  }
	
	@GetMapping(USER_DETAILS_MAPPING)
	public String getUserDetails(@PathVariable("userId") final Long userId, Model model) {
	  log.debug("In getUserDetails: " + userId);
	  
	  model.addAttribute(USER_MODEL_ATTRIBUTE, userService.getUser(userId));
	  
	  return USER_DETAILS_VIEW;
	}
}
