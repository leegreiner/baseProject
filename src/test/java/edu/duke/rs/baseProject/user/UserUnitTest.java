package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import org.junit.Test;

public class UserUnitTest {
  @Test
  public void whenFirstAndLastNamePresent_displayNameShowsBoth() {
    final User user = new User();
    
    user.setFirstName("John");
    user.setLastName("Smith");
    
    assertThat(user.getDisplayName(), equalTo(user.getFirstName() + " " + user.getLastName()));
  }

  @Test
  public void whenFirstNameMiddleInitialAndLastNamePresent_displayNameShowsAll() {
    final User user = new User();
    
    user.setFirstName("John");
    user.setMiddleInitial("M");
    user.setLastName("Smith");
    
    assertThat(user.getDisplayName(), equalTo(user.getFirstName() + " " +user.getMiddleInitial() + " " + user.getLastName()));
  }
}
