package edu.duke.rs.baseProject.home;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.duke.rs.baseProject.BaseWebController;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Controller
public class HomeController extends BaseWebController {
  public static final String HOME_MAPPING = "/home";
	static final String HOME_VIEW = "home/home";
	
	@GetMapping(HOME_MAPPING)
	public String home() {
		log.debug("In home()");
		return HOME_VIEW;
	}
}
