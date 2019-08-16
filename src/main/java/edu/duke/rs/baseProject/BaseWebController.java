package edu.duke.rs.baseProject;

import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public abstract class BaseWebController {
  public static final String REDIRECT_PREFIX = "redirect:";
  public static final String FLASH_ERROR_MESSAGE = "errorMessage";
  public static final String FLASH_FEEDBACK_MESSAGE = "feedbackMessage";
  public static final String FLASH_WARNING_MESSAGE = "warningMessage";
  @Autowired
  private MessageSource messageSource;
  
  protected BaseWebController() {}
  
  // for testing only
  protected BaseWebController(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }
  
  protected void addErrorMessage(Model model, String code, Object...params) {
    final Locale current = LocaleContextHolder.getLocale();
    final String localizedErrorMessage = messageSource.getMessage(code, params, code, current);

    model.addAttribute(FLASH_ERROR_MESSAGE, localizedErrorMessage);
  }
  
  protected void addFeedbackMessage(Model model, String code, Object...params) {
    final Locale current = LocaleContextHolder.getLocale();
    final String localizedErrorMessage = messageSource.getMessage(code, params, code, current);

    model.addAttribute(FLASH_FEEDBACK_MESSAGE, localizedErrorMessage);
  }
  
  protected void addWarningMessage(Model model, String code, Object...params) {
    final Locale current = LocaleContextHolder.getLocale();
    final String localizedErrorMessage = messageSource.getMessage(code, params, code, current);

    model.addAttribute(FLASH_WARNING_MESSAGE, localizedErrorMessage);
  }
  
  protected void addErrorMessage(RedirectAttributes model, String code, Object...params) {
    final Locale current = LocaleContextHolder.getLocale();
    final String localizedErrorMessage = messageSource.getMessage(code, params, code, current);

    model.addFlashAttribute(FLASH_ERROR_MESSAGE, localizedErrorMessage);
  }
  
  protected void addFeedbackMessage(RedirectAttributes model, String code, Object...params) {
    final Locale current = LocaleContextHolder.getLocale();
    final String localizedErrorMessage = messageSource.getMessage(code, params, code, current);

    model.addFlashAttribute(FLASH_FEEDBACK_MESSAGE, localizedErrorMessage);
  }
  
  protected void addWarningMessage(RedirectAttributes model, String code, Object...params) {
    final Locale current = LocaleContextHolder.getLocale();
    final String localizedErrorMessage = messageSource.getMessage(code, params, code, current);

    model.addFlashAttribute(FLASH_WARNING_MESSAGE, localizedErrorMessage);
  }

  protected String createRedirectViewPath(String path) {
    StringBuilder builder = new StringBuilder();
    builder.append(REDIRECT_PREFIX);
    builder.append(path);
    return builder.toString();
  }
}
