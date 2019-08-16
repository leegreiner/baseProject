package edu.duke.rs.baseProject.security.password;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import edu.duke.rs.baseProject.security.password.PasswordNeverExpiresStrategy;
import edu.duke.rs.baseProject.user.User;

public class PasswordNeverExpiresStrategyUnitTest {
  @Test
  public void whenArbitraryUserPassedToIsPasswordExpired_thenFalseIsReturned() {
    final User user = new User();
    final PasswordNeverExpiresStrategy strategy = new PasswordNeverExpiresStrategy();
    
    assertThat(strategy.isPasswordExpired(user), equalTo(false));
  }
}
