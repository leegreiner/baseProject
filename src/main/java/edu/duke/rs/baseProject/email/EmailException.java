package edu.duke.rs.baseProject.email;

import edu.duke.rs.baseProject.exception.ApplicationException;

public class EmailException extends ApplicationException {
  private static final long serialVersionUID = 1L;

  public EmailException() {
    super();
  }

  public EmailException(final String message, final Object[] messageArguments) {
    super(message, messageArguments);
  }

  public EmailException(final Throwable cause) {
    super(cause);
  }

  public EmailException(final String message, final Throwable cause, final Object[] messageArguments) {
    super(message, cause, messageArguments);
  }

  public EmailException(final String message, final Throwable cause, final boolean enableSuppression,
      final boolean writableStackTrace, final Object[] messageArguments) {
    super(message, cause, enableSuppression, writableStackTrace, messageArguments);
  }
}
