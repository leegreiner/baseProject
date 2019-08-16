package edu.duke.rs.baseProject.exception;

public class ConstraintViolationException extends ApplicationException {
  private static final long serialVersionUID = 1L;

  public ConstraintViolationException() {}

  public ConstraintViolationException(String message, Object[] messageArguments) {
    super(message, messageArguments);
  }

  public ConstraintViolationException(Throwable cause) {
    super(cause);
  }

  public ConstraintViolationException(String message, Throwable cause, Object[] messageArguments) {
    super(message, cause, messageArguments);
  }

  public ConstraintViolationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
      Object[] messageArguments) {
    super(message, cause, enableSuppression, writableStackTrace, messageArguments);
  }

}
