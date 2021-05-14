package edu.duke.rs.baseProject.role;

import edu.duke.rs.baseProject.converter.AbstractEnumConverter;
import edu.duke.rs.baseProject.converter.PersistableEnum;

public enum PrivilegeName implements PersistableEnum<String> {
  EDIT_USERS("Edit Users"), VIEW_USERS("View Users");
  
  private final String value;
  
  private PrivilegeName(final String value) {
    this.value = value;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public static class Converter extends AbstractEnumConverter<PrivilegeName, String> {
    public Converter() {
        super(PrivilegeName.class);
    }
  }
}
