package edu.duke.rs.baseProject.role;

public enum PrivilegeName {
  EDIT_USERS("Edit Users"), LIST_USERS("Read Users");
  
  private final String value;
  
  private PrivilegeName(final String value) {
    this.value = value;
  }
  
  public String getValue() {
    return this.value;
  }
}
