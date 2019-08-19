package edu.duke.rs.baseProject.exception;

public class UnrecoverableException extends ApplicationException {
  private static final long serialVersionUID = 1L;

  public UnrecoverableException() {
    // TODO Auto-generated constructor stub
  }

  public UnrecoverableException(String message, Object[] messageArguments) {
    super(message, messageArguments);
  }

  public UnrecoverableException(Throwable cause) {
    super(cause);
  }

  public UnrecoverableException(String message, Throwable cause, Object[] messageArguments) {
    super(message, cause, messageArguments);
  }

  public UnrecoverableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace,
      Object[] messageArguments) {
    super(message, cause, enableSuppression, writableStackTrace, messageArguments);
  }

}
