package edu.duke.rs.baseProject.security.password;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.time.LocalDateTime;

import org.junit.Test;

import edu.duke.rs.baseProject.user.User;

public class DaysSinceLastChangePasswordExpirationStrategyUnitTest {
  @Test
  public void whenLastPassordChangeDateIsNull_thenPasswordExpired() {
    final DaysSinceLastChangePasswordExpirationStrategy strategy = new DaysSinceLastChangePasswordExpirationStrategy();
    strategy.setDaysSinceLastPasswordChange(1);
    
    assertThat(strategy.isPasswordExpired(new User()), equalTo(true));
  }
  
  @Test
  public void whenLastPasswordChangeBeforeDeadline_thenPasswordExpired() {
    final DaysSinceLastChangePasswordExpirationStrategy strategy = new DaysSinceLastChangePasswordExpirationStrategy();
    strategy.setDaysSinceLastPasswordChange(90);
    final User user = new User();
    user.setLastPasswordChange(LocalDateTime.now().minusDays(strategy.getDaysSinceLastPasswordChange() + 1));
    
    assertThat(strategy.isPasswordExpired(user), equalTo(true));
  }
  
  @Test
  public void whenLastPasswordChangeBeforeDeadline_thenPasswordNotExpired() {
    final DaysSinceLastChangePasswordExpirationStrategy strategy = new DaysSinceLastChangePasswordExpirationStrategy();
    strategy.setDaysSinceLastPasswordChange(90);
    final User user = new User();
    user.setLastPasswordChange(LocalDateTime.now().minusDays(strategy.getDaysSinceLastPasswordChange() - 1));
    
    assertThat(strategy.isPasswordExpired(user), equalTo(false));
  }
}
