package edu.duke.rs.baseProject.user.passwordReset;

import org.springframework.context.ApplicationEvent;

import edu.duke.rs.baseProject.user.User;
import lombok.Getter;

@Getter
public class PasswordResetInitiatedEvent extends ApplicationEvent {
  private static final long serialVersionUID = 1L;
  private User user;
  
  public PasswordResetInitiatedEvent(final Object source, final User user) {
    super(source);
    this.user = user;
  }
}
