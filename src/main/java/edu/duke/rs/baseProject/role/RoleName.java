package edu.duke.rs.baseProject.role;

import edu.duke.rs.baseProject.converter.AbstractEnumConverter;
import edu.duke.rs.baseProject.converter.PersistableEnum;

public enum RoleName implements PersistableEnum<String> {
  ADMINISTRATOR("Administrator"), USER("User");
  
  private final String value;
  
  private RoleName(final String value) {
    this.value = value;
  }
  
  public String getValue() {
    return this.value;
  }
  
  public static class Converter extends AbstractEnumConverter<RoleName, String> {
    public Converter() {
        super(RoleName.class);
    }
  }
}
