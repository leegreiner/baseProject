package edu.duke.rs.baseProject.security.password;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.user.User;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Profile({"!samlSecurity", "!test"})
@Component
public class DaysSinceLastChangePasswordExpirationStrategy implements PasswordExpirationStrategy {
  @Value("${app.passwordExpirationDays}")
  private long daysSinceLastPasswordChange;
  
  @Override
  public boolean isPasswordExpired(@NotNull User user) {
    return user.getLastPasswordChange() == null ? true :
      user.getLastPasswordChange().plusDays(daysSinceLastPasswordChange).isBefore(LocalDateTime.now()) ? true : false;
  }
}
