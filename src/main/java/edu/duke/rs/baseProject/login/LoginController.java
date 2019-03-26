package edu.duke.rs.baseProject.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.duke.rs.baseProject.BaseWebController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController extends BaseWebController {
	public static final String LOGIN_VIEW = "login";
	public static final String LOGIN_MAPPING = "/loginPage";
	
	@GetMapping(LOGIN_MAPPING)
	public String loginPage() {
		log.debug("In login()");
		return LOGIN_VIEW;
	}
}
