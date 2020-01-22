package edu.duke.rs.baseProject.user.passwordReset;

import java.util.UUID;

import javax.validation.Valid;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.index.IndexController;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.user.UserController;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
@Profile("!samlSecurity")
public class PasswordResetController extends BaseWebController {
  public static final String PASSWORD_RESET_INITIATE_MAPPING = UserController.USERS_MAPPING + "/pwdreset";
  public static final String PASSWORD_RESET_INITIATE_VIEW = "users/pwdreset/initiatePasswordReset";
  public static final String PASSWORD_RESET_VIEW = "users/pwdreset/passwordReset";
  public static final String PASSWORD_RESET_MODEL_ATTRIBUTE = "pwdreset";
  public static final String PASSWORD_RESET_ID_REQUEST_PARAM = "id";
  private transient final PasswordResetService passwordResetService;
  private transient final SecurityUtils securityUtils;
  
  public PasswordResetController(final PasswordResetService passwordResetService, final SecurityUtils securityUtils) {
    this.passwordResetService = passwordResetService;
    this.securityUtils = securityUtils;
  }
  
  @GetMapping(PASSWORD_RESET_INITIATE_MAPPING)
  public String getPasswordReset(@RequestParam(name = PASSWORD_RESET_ID_REQUEST_PARAM, required=false) final UUID passwordChangeId,
      final Model model) {
    log.debug("In getPasswordReset(): " + passwordChangeId);
    final boolean resettingPassword = passwordChangeId == null ? false : true;
    String view;
    
    if (resettingPassword) {
      final PasswordResetDto passwordResetDto = new PasswordResetDto();
      passwordResetDto.setPasswordChangeId(passwordChangeId);
      model.addAttribute(PASSWORD_RESET_MODEL_ATTRIBUTE, passwordResetDto);
      view = PASSWORD_RESET_VIEW;
    } else {
      model.addAttribute(PASSWORD_RESET_MODEL_ATTRIBUTE, new InitiatePasswordResetDto());
      view = PASSWORD_RESET_INITIATE_VIEW;
    }
    
    return view;
  }
  
  @PostMapping(PASSWORD_RESET_INITIATE_MAPPING)
  public String initiateResetPassword(@Valid @ModelAttribute(name = PASSWORD_RESET_MODEL_ATTRIBUTE) final InitiatePasswordResetDto passwordResetDto,
      final BindingResult result, final Model model, final RedirectAttributes attributes) {
    if (result.hasErrors()) {
      this.addErrorMessage(model, "error.pleaseCorrectErrors", (Object[])null);
      return PASSWORD_RESET_INITIATE_VIEW;
    }
    
    try {
      this.passwordResetService.initiatePasswordReset(passwordResetDto);
    } catch (final NotFoundException nfe) {
      this.addErrorMessage(model, "error.userWithEmailNotFound", (Object[])null);
      return PASSWORD_RESET_INITIATE_VIEW;
    } catch (final ConstraintViolationException cve) {
      this.addErrorMessage(model, cve.getMessage(), cve.getMessageArguments());
      return PASSWORD_RESET_INITIATE_VIEW;
    }
    
    this.addFeedbackMessage(attributes, "message.passwordResetInitiated", (Object[])null);

    return securityUtils.userIsAuthenticated() ? 
        UriComponentsBuilder.fromPath(REDIRECT_PREFIX + HomeController.HOME_MAPPING).toUriString() :
          UriComponentsBuilder.fromPath(REDIRECT_PREFIX + IndexController.INDEX_MAPPING).toUriString();
  }
  
  @PutMapping(PASSWORD_RESET_INITIATE_MAPPING)
  public String resetPassword(@Valid @ModelAttribute(name = PASSWORD_RESET_MODEL_ATTRIBUTE) final PasswordResetDto passwordResetDto,
      final BindingResult result, final Model model, final RedirectAttributes attributes) {
    if (result.hasErrors()) {
      return PASSWORD_RESET_VIEW;
    }
    
    try {
      this.passwordResetService.processPasswordReset(passwordResetDto);
    } catch (final NotFoundException nfe) {
      this.addErrorMessage(model, "error.userWithPasswordResetIdNotFound", (Object[])null);
      return PASSWORD_RESET_VIEW;
    } catch (final ConstraintViolationException cve) {
      this.addErrorMessage(model, cve.getMessage(), cve.getMessageArguments());
      return PASSWORD_RESET_VIEW;
    }
    
    this.addFeedbackMessage(attributes, "message.passwordResetCompleted", (Object[])null);
    
    return securityUtils.userIsAuthenticated() ? 
        UriComponentsBuilder.fromPath(REDIRECT_PREFIX + HomeController.HOME_MAPPING).toUriString() :
          UriComponentsBuilder.fromPath(REDIRECT_PREFIX + IndexController.INDEX_MAPPING).toUriString();
  }
}
