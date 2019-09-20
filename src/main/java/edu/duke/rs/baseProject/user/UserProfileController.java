package edu.duke.rs.baseProject.user;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserProfileController extends BaseWebController {
  public static final String USER_PROFILE_VIEW = "users/userProfile";
  public static final String USER_PROFILE_MAPPING = "/userProfile";
  public static final String USER_PROFILE_ATTRIBUTE = "userProfile";
  private transient final UserService userService;
  
  public UserProfileController(final UserService userService) {
    this.userService = userService;
  }
  
  @GetMapping(USER_PROFILE_MAPPING)
  public String userProfile(Model model) {
    log.debug("In userProfile()");
    final UserProfile userProfile = userService.getUserProfile();
    
    model.addAttribute(USER_PROFILE_ATTRIBUTE, userProfile);
    
    return USER_PROFILE_VIEW;
  }
  
  @PutMapping(USER_PROFILE_MAPPING)
  public String updateUserProfile(@Valid @ModelAttribute final UserProfile userProfile, final BindingResult result,
      final Model model, final RedirectAttributes attributes, final HttpSession httpSession) {
    log.debug("In updateUserProfile(): ");
    
    if (result.hasErrors()) {
      this.addErrorMessage(model, "error.pleaseCorrectErrors", (Object[])null);
      return USER_PROFILE_VIEW;
    }
    
    final AppPrincipal principal = SecurityUtils.getPrincipal().orElseThrow(() ->  new IllegalArgumentException("error.principalNotFound"));
    
    try {
      userService.updateUserProfile(userProfile);
    } catch(final Exception exception) {
      this.addErrorMessage(model, exception.getMessage(), (Object[])null);
      return USER_PROFILE_VIEW;
    }
    
    principal.setTimeZone(userProfile.getTimeZone());
    
    HttpUtils.notifySessionOfChange(httpSession);
    
    this.addFeedbackMessage(attributes, "message.userProfile.updated", (Object[])null);
    
    return this.createRedirectViewPath(HomeController.HOME_MAPPING);
  }
}
