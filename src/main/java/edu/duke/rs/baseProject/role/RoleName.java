package edu.duke.rs.baseProject.role;

public enum RoleName {
  ADMINISTRATOR("Administrator"), USER("User");
  
  private final String name;
  
  private RoleName(final String name) {
    this.name = name;
  }
  
  public String getName() {
    return this.name;
  }
}
