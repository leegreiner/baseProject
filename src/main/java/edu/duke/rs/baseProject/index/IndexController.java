package edu.duke.rs.baseProject.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class IndexController {
	@GetMapping("/")
	public String index() {
		log.debug("In index()");
		return "index";
	}
}
