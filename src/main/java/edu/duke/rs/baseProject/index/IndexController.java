package edu.duke.rs.baseProject.index;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.home.HomeController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class IndexController extends BaseWebController {
	public static final String INDEX_VIEW = "index";
	public static final String INDEX_MAPPING = "/";
	
	@GetMapping(INDEX_MAPPING)
	public String index(Principal principal) {
		log.trace("In index()");
		
		if (principal == null) {
			return INDEX_VIEW;
		} else {
			return createRedirectViewPath(HomeController.HOME_MAPPING);
		}
	}
}
