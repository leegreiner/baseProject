package edu.duke.rs.baseProject.role;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

public class PrivilegeNameConverterUnitTest {
  @Test
  public void whenNullPassedToConvertToDatabaseColumn_thenNullReturned() {
    assertThat(new PrivilegeNameConverter().convertToDatabaseColumn(null), nullValue());
  }
  
  @Test
  public void whenPrivilegeNamePassedToConvertToDatabaseColumn_thenRolesNameIsReturned() {
    assertThat(new PrivilegeNameConverter().convertToDatabaseColumn(PrivilegeName.EDIT_USERS), equalTo(PrivilegeName.EDIT_USERS.getValue()));
  }
  
  @Test
  public void whenNullPassedToConvertToEntityAttribute_thenNullReturned() {
    assertThat(new PrivilegeNameConverter().convertToEntityAttribute(null), nullValue());
  }
  
  @Test
  public void whenInvalidStringPassedToConvertToEntityAttribute_thenIllegalArgumentExceptionThrown() {
    assertThrows(IllegalArgumentException.class, () -> new PrivilegeNameConverter().convertToEntityAttribute("somecrazystring"));
  }
  
  @Test
  public void whenPrivilegeNamePassedToConvertToEntityAttribute_thenRolesNameIsReturned() {
    assertThat(new PrivilegeNameConverter().convertToEntityAttribute(PrivilegeName.VIEW_USERS.getValue()), equalTo(PrivilegeName.VIEW_USERS));
  }
}
