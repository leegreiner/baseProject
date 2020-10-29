package edu.duke.rs.baseProject.security.password;

import java.time.LocalDateTime;

import javax.validation.constraints.NotNull;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import edu.duke.rs.baseProject.config.ApplicationProperties;
import edu.duke.rs.baseProject.user.User;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
@Profile({"!samlSecurity", "!test"})
@Component
public class DaysSinceLastChangePasswordExpirationStrategy implements PasswordExpirationStrategy {
  private transient final ApplicationProperties applicationProperties;
  
  @Override
  public boolean isPasswordExpired(@NotNull User user) {
    return user.getLastPasswordChange() == null ? true :
      user.getLastPasswordChange().plusDays(applicationProperties
          .getSecurity().getPassword().getExpirationDays()).isBefore(LocalDateTime.now()) ? true : false;
  }
}
