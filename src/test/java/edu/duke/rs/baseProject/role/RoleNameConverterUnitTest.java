package edu.duke.rs.baseProject.role;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RoleNameConverterUnitTest {
  @Test
  public void whenNullPassedToConvertToDatabaseColumn_thenNullReturned() {
    assertThat(new RoleNameConverter().convertToDatabaseColumn(null), nullValue());
  }
  
  @Test
  public void whenRoleNamePassedToConvertToDatabaseColumn_thenRolesNameIsReturned() {
    assertThat(new RoleNameConverter().convertToDatabaseColumn(RoleName.ADMINISTRATOR), equalTo(RoleName.ADMINISTRATOR.getValue()));
  }
  
  @Test
  public void whenNullPassedToConvertToEntityAttribute_thenNullReturned() {
    assertThat(new RoleNameConverter().convertToEntityAttribute(null), nullValue());
  }
  
  @Test(expected = IllegalArgumentException.class)
  public void whenInvalidStringPassedToConvertToEntityAttribute_thenIllegalArgumentExceptionThrown() {
    new RoleNameConverter().convertToEntityAttribute("somecrazystring");
  }
  
  @Test
  public void whenRoleNamePassedToConvertToEntityAttribute_thenRolesNameIsReturned() {
    assertThat(new RoleNameConverter().convertToEntityAttribute(RoleName.ADMINISTRATOR.getValue()), equalTo(RoleName.ADMINISTRATOR));
  }
}
