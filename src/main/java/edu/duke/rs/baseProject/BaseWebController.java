package edu.duke.rs.baseProject;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import edu.duke.rs.baseProject.config.GoogleProperties;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class BaseWebController {
  private static final String VIEW_REDIRECT_PREFIX = "redirect:";
  public static final String FLASH_ERROR_MESSAGE = "errorMessage";
  public static final String FLASH_FEEDBACK_MESSAGE = "feedbackMessage";
  public static final String FLASH_WARNING_MESSAGE = "warningMessage";
  @Autowired
  private GoogleProperties googleProperties;
  @Autowired
  private MessageSource messageSource;
  
  protected void addErrorMessage(Model model, String code, Object...params) {
    log.debug("adding error message with code: " + code + " and params: " + params);
    final Locale current = LocaleContextHolder.getLocale();
    log.debug("Current locale is " + current);
    final String localizedErrorMessage = messageSource.getMessage(code, params, current);
    log.debug("Localized message is: " + localizedErrorMessage);
    model.addAttribute(FLASH_ERROR_MESSAGE, localizedErrorMessage);
  }
  
  protected void addFeedbackMessage(Model model, String code, Object...params) {
    log.debug("adding feedback message with code: " + code + " and params: " + params);
    final Locale current = LocaleContextHolder.getLocale();
    log.debug("Current locale is " + current);
    final String localizedErrorMessage = messageSource.getMessage(code, params, current);
    log.debug("Localized message is: " + localizedErrorMessage);
    model.addAttribute(FLASH_FEEDBACK_MESSAGE, localizedErrorMessage);
  }
  
  protected void addWarningMessage(Model model, String code, Object...params) {
    log.debug("adding warning message with code: " + code + " and params: " + params);
    final Locale current = LocaleContextHolder.getLocale();
    log.debug("Current locale is " + current);
    final String localizedErrorMessage = messageSource.getMessage(code, params, current);
    log.debug("Localized message is: " + localizedErrorMessage);
    model.addAttribute(FLASH_WARNING_MESSAGE, localizedErrorMessage);
  }
  
  protected void addErrorMessage(RedirectAttributes model, String code, Object...params) {
    log.debug("adding error message with code: " + code + " and params: " + params);
    final Locale current = LocaleContextHolder.getLocale();
    log.debug("Current locale is " + current);
    final String localizedErrorMessage = messageSource.getMessage(code, params, current);
    log.debug("Localized message is: " + localizedErrorMessage);
    model.addFlashAttribute(FLASH_ERROR_MESSAGE, localizedErrorMessage);
  }
  
  protected void addFeedbackMessage(RedirectAttributes model, String code, Object...params) {
    log.debug("adding feedback message with code: " + code + " and params: " + params);
    final Locale current = LocaleContextHolder.getLocale();
    log.debug("Current locale is " + current);
    final String localizedErrorMessage = messageSource.getMessage(code, params, current);
    log.debug("Localized message is: " + localizedErrorMessage);
    model.addFlashAttribute(FLASH_FEEDBACK_MESSAGE, localizedErrorMessage);
  }
  
  protected void addWarningMessage(RedirectAttributes model, String code, Object...params) {
    log.debug("adding warning message with code: " + code + " and params: " + params);
    final Locale current = LocaleContextHolder.getLocale();
    log.debug("Current locale is " + current);
    final String localizedErrorMessage = messageSource.getMessage(code, params, current);
    log.debug("Localized message is: " + localizedErrorMessage);
    model.addFlashAttribute(FLASH_WARNING_MESSAGE, localizedErrorMessage);
  }

  protected String createRedirectViewPath(String path) {
    StringBuilder builder = new StringBuilder();
    builder.append(VIEW_REDIRECT_PREFIX);
    builder.append(path);
    return builder.toString();
  }
  
  @ModelAttribute("googleProperties")
  public GoogleProperties getGoogleProperties() {
    return this.googleProperties;
  }

}
