package edu.duke.rs.baseProject.error;

import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.servlet.ModelAndView;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.exception.ApplicationException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@ControllerAdvice(assignableTypes = {BaseWebController.class})
public class ExceptionController extends BaseWebController {
  public static final String EXCEPTION_ERROR_VIEW = "error/exceptionError";
  public static final String EXCEPTION_MESSAGE_ATTRIBUTE = "errorMessage";
  public static final String UNKNOWN_ERROR_PROPERTY = "unknownError";
  private transient final MessageSource messageSource;

  @InitBinder
  public void initBinder (WebDataBinder binder) {
    // trim all string in incoming model attributes
    binder.registerCustomEditor(String.class, new StringTrimmerEditor(true));
  }
  
  @ExceptionHandler(ApplicationException.class)
  public ModelAndView handleApplicationException(final HttpServletRequest request,
      final HttpServletResponse response, final ApplicationException ae) {
    log.error("In handleApplicationException()", () -> ae);
    
    return processError(ae.getMessage(), ae.getMessageArguments());
  }
  
  @ExceptionHandler(Exception.class)
  public ModelAndView handleException(final HttpServletRequest request,
      final HttpServletResponse response, final Exception exception) throws Exception {
    log.error("In handleException()", exception);
    
    return processError(UNKNOWN_ERROR_PROPERTY, (Object[])null);
  }
  
  private ModelAndView processError(final String message, final Object[] messageProperties) {
    final ModelAndView mav = new ModelAndView();
    mav.setViewName(EXCEPTION_ERROR_VIEW);
    mav.addObject(EXCEPTION_MESSAGE_ATTRIBUTE, convertToMessage(message, messageProperties));
    
    return mav;
  }
  
  private String convertToMessage(final String code, final Object[] args) {
    return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
  }
}
