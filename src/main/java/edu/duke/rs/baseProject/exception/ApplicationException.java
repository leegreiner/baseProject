package edu.duke.rs.baseProject.exception;

public abstract class ApplicationException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private Object[] messageArguments;

  public ApplicationException() {
    super();
  }
  
  public ApplicationException(final String message, final Object[] messageArguments) {
    super(message);
    this.messageArguments = messageArguments;
  }

  public ApplicationException(final Throwable cause) {
    super(cause);
  }

  public ApplicationException(final String message, final Throwable cause, final Object[] messageArguments) {
    super(message, cause);
    this.messageArguments = messageArguments;
  }

  public ApplicationException(final String message, final Throwable cause, final boolean enableSuppression,
      final boolean writableStackTrace, final Object[] messageArguments) {
    super(message, cause, enableSuppression, writableStackTrace);
    this.messageArguments = messageArguments;
    
  }
  
  public Object[] getMessageArguments() {
    return this.messageArguments;
  }
}
