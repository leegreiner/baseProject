package edu.duke.rs.baseProject.exception;

public class NotFoundException extends ApplicationException {

  private static final long serialVersionUID = 1L;

  public NotFoundException() {
    super();
  }

  public NotFoundException(final String message, final Object[] messageArguments) {
    super(message, messageArguments);
  }

  public NotFoundException(final Throwable cause) {
    super(cause);
  }

  public NotFoundException(final String message, final Throwable cause, final Object[] messageArguments) {
    super(message, cause, messageArguments);
  }

  public NotFoundException(final String message, final Throwable cause, final boolean enableSuppression,
      final boolean writableStackTrace, final Object[] messageArguments) {
    super(message, cause, enableSuppression, writableStackTrace, messageArguments);
  }

}
