package edu.duke.rs.baseProject.error;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import edu.duke.rs.baseProject.BaseRestController;
import edu.duke.rs.baseProject.exception.ApplicationException;
import edu.duke.rs.baseProject.security.ErrorInfo;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestControllerAdvice(assignableTypes = {BaseRestController.class})
public class RestExceptionController extends BaseRestController {
  private transient final MessageSource messageSource;
  
  @ResponseBody
  @ExceptionHandler(ApplicationException.class)
  public ResponseEntity<ErrorInfo> handleApplicationException(HttpServletRequest request, HttpServletResponse response,
      Exception exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(getBody(request, response, exception));
  }
  
  @ResponseBody
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorInfo> handleException(HttpServletRequest request, HttpServletResponse response,
      Exception exception) {
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(getBody(request, response, exception));
  }

  private ErrorInfo getBody(HttpServletRequest request, HttpServletResponse response,
      Exception ex) {
    final String url = request.getRequestURL().toString();
    Object[] params = (Object[])null;
    
    if (ex instanceof ApplicationException) {
      params = ((ApplicationException) ex).getMessageArguments();
    }
    
    return new ErrorInfo(convertToMessage(ex.getMessage(), params), url);
  }
  
  private String convertToMessage(final String code, final Object[] args) {
    return messageSource.getMessage(code, args, code, LocaleContextHolder.getLocale());
  }
}
