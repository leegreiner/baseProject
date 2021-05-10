package edu.duke.rs.baseProject.login;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Controller
public class LoginController extends BaseWebController {
  public static final String LOGIN_MAPPING = "/login";
	static final String LOGIN_VIEW = "login";
	private transient final SecurityUtils securityUtils;

	@GetMapping(LOGIN_MAPPING)
	public String loginPage() {
		log.debug("In login()");
		return securityUtils.userIsAuthenticated() ? this.createRedirectViewPath(HomeController.HOME_MAPPING) : LOGIN_VIEW;
	}
}
