package edu.duke.rs.baseProject.security.password;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import edu.duke.rs.baseProject.config.ApplicationProperties;
import edu.duke.rs.baseProject.user.User;

public class DaysSinceLastChangePasswordExpirationStrategyUnitTest {
  private ApplicationProperties applicationProperties;
  
  @BeforeEach
  public void init() {
    this.applicationProperties = new ApplicationProperties();
  }
  
  @Test
  public void whenLastPassordChangeDateIsNull_thenPasswordExpired() {
    applicationProperties.getSecurity().getPassword().setExpirationDays(Long.valueOf(1));
    final DaysSinceLastChangePasswordExpirationStrategy strategy = new DaysSinceLastChangePasswordExpirationStrategy(applicationProperties);
    
    assertThat(strategy.isPasswordExpired(new User()), equalTo(true));
  }
  
  @Test
  public void whenLastPasswordChangeBeforeDeadline_thenPasswordExpired() {
    applicationProperties.getSecurity().getPassword().setExpirationDays(Long.valueOf(90));
    final DaysSinceLastChangePasswordExpirationStrategy strategy = new DaysSinceLastChangePasswordExpirationStrategy(applicationProperties);
    final User user = new User();
    user.setLastPasswordChange(LocalDateTime.now().minusDays(applicationProperties.getSecurity().getPassword().getExpirationDays() + 1));
    
    assertThat(strategy.isPasswordExpired(user), equalTo(true));
  }
  
  @Test
  public void whenLastPasswordChangeBeforeDeadline_thenPasswordNotExpired() {
    applicationProperties.getSecurity().getPassword().setExpirationDays(Long.valueOf(90));
    final DaysSinceLastChangePasswordExpirationStrategy strategy = new DaysSinceLastChangePasswordExpirationStrategy(applicationProperties);
    final User user = new User();
    user.setLastPasswordChange(LocalDateTime.now().minusDays(applicationProperties.getSecurity().getPassword().getExpirationDays() - 1));
    
    assertThat(strategy.isPasswordExpired(user), equalTo(false));
  }
}
