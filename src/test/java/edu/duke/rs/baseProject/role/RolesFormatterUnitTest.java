package edu.duke.rs.baseProject.role;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class RolesFormatterUnitTest {
  private static final RolesFormatter formatter = new RolesFormatter();
  
  @Test
  public void whenNullRolesPassed_thenEmptyStringReturned() {
    assertThat("", equalTo(formatter.print(null, Locale.getDefault())));
  }
  
  @Test
  public void whenNoRolesPassed_thenEmptyStringReturned() {
    assertThat("", equalTo(formatter.print(new HashSet<Role>(), Locale.getDefault())));
  }
  
  @Test
  public void whenRolesPassed_thenCommaSeparatedRoleNamesReturned() {
    final Set<Role> roles = new HashSet<Role>();
    final Role role1 = new Role(RoleName.USER);
    final Role role2 = new Role(RoleName.ADMINISTRATOR);
    roles.add(role1);
    roles.add(role2);
    
    final String actual = formatter.print(roles, Locale.getDefault());
    
    assertThat(actual, equalTo(RoleName.ADMINISTRATOR.getValue() + ", " + RoleName.USER.getValue()));
  }
  
  @Test
  public void whenParseCalled_thenNotImplementedExceptionThrown() throws ParseException {
    assertThrows(ParseException.class, () -> formatter.parse("abc", Locale.getDefault()));
  }
}
