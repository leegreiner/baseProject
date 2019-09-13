package edu.duke.rs.baseProject.role;

public enum RoleName {
  ADMINISTRATOR("Administrator"), USER("User");
  
  private final String value;
  
  private RoleName(final String value) {
    this.value = value;
  }
  
  public String getValue() {
    return this.value;
  }
}
