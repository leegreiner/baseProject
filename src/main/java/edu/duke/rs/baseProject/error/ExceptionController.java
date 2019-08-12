package edu.duke.rs.baseProject.error;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.exception.ApplicationException;
import edu.duke.rs.baseProject.security.ErrorInfo;
import edu.duke.rs.baseProject.util.HttpUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionController extends BaseWebController {
  public static final String EXCEPTION_ERROR_VIEW = "/error/exceptionError";
  public static final String EXCEPTION_MESSAGE_ATTRIBUTE = "errorMessage";
  private transient final MessageSource messageSource;
  
  public ExceptionController(final MessageSource messageSource) {
    this.messageSource = messageSource;
  }
  
  @ExceptionHandler(ApplicationException.class)
  public ModelAndView handleApplicationException(HttpServletRequest request, HttpServletResponse response,
      ApplicationException ae) {
    log.error("In handleApplicationException()", ae);
    
    if (AnnotationUtils.findAnnotation(ae.getClass(), ResponseStatus.class) != null) {
      throw ae;
    }
    
    if (HttpUtils.isAjaxRequest(request)) {
      return processAjaxRequest(request, response, ae);
    } else {
      final ModelAndView mav = new ModelAndView();
      mav.setViewName(EXCEPTION_ERROR_VIEW);
      mav.addObject(EXCEPTION_MESSAGE_ATTRIBUTE, convertToMessage(ae.getMessage(), ae.getMessageArguments()));
      
      return mav;
    }
  }
  
  private ModelAndView processAjaxRequest(HttpServletRequest request, HttpServletResponse response,
      Exception ex) {
    response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
    final MappingJackson2JsonView view = new MappingJackson2JsonView();
    final String url = request.getRequestURL().toString();
    final Map<String, Object> map = new HashMap<String, Object>();
    
    map.put("error", new ErrorInfo(url, ex.getMessage()));
    view.setAttributesMap(map);
    
    return new ModelAndView(view);
  }
  
  private String convertToMessage(final String code, final Object[] args) {
    return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
  }
}
