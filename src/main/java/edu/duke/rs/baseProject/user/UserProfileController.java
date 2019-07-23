package edu.duke.rs.baseProject.user;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.TimeZoneDisplay;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserProfileController extends BaseWebController {
  public static final String USER_PROFILE_VIEW = "/users/userProfile";
  public static final String USER_PROFILE_MAPPING = "/userProfile";
  public static final String USER_PROFILE_ATTRIBUTE = "userProfile";
  public static final String TIME_ZONES_ATTRIBUTE = "timeZones";
  
  
  @GetMapping(USER_PROFILE_MAPPING)
  public String userProfile(Model model) {
    log.trace("In userProfile()");
    final UserProfile userProfile = new UserProfile(SecurityUtils.getPrincipal().getTimeZone());
    model.addAttribute(USER_PROFILE_ATTRIBUTE, userProfile);
    model.addAttribute(TIME_ZONES_ATTRIBUTE, TimeZoneDisplay.getDisplayableTimeZones());
    
    return USER_PROFILE_VIEW;
  }
  
  @PutMapping(USER_PROFILE_MAPPING)
  public String updateUserProfile(@Valid @ModelAttribute UserProfile userProfile, final HttpSession httpSession) {
    log.trace("In updateUserProfile(): " + userProfile.toString());

    SecurityUtils.getPrincipal().setTimeZone(userProfile.getTimeZone());
    // need to tickle the session to have Spring Session save the changes to the principal
    httpSession.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
    
    return this.createRedirectViewPath(HomeController.HOME_MAPPING);
  }
}
