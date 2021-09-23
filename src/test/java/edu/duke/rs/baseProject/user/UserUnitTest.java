package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.jupiter.api.Test;

import edu.duke.rs.baseProject.AbstractBaseTest;

public class UserUnitTest extends AbstractBaseTest {
  @Test
  public void whenFirstAndLastNamePresent_displayNameShowsBoth() {
    final User user = new User();
    
    user.setFirstName(easyRandom.nextObject(String.class));
    user.setLastName(easyRandom.nextObject(String.class));
    
    assertThat(user.getDisplayName(), equalTo(user.getFirstName() + " " + user.getLastName()));
  }

  @Test
  public void whenFirstNameMiddleInitialAndLastNamePresent_displayNameShowsAll() {
    final User user = new User();
    
    user.setFirstName(easyRandom.nextObject(String.class));
    user.setMiddleInitial("M");
    user.setLastName(easyRandom.nextObject(String.class));
    
    assertThat(user.getDisplayName(), equalTo(user.getFirstName() + " " +user.getMiddleInitial() + " " + user.getLastName()));
  }
}
