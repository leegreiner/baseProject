package edu.duke.rs.baseProject.email;

import lombok.Getter;

@Getter
public enum MessageType {
  TEST("testEmail"),
  PASSWORD_RESET_INITIATED("passwordResetInitiated"),
  NEW_SAML_USER("newSamlUser"),
  NEW_USER("newUser");
  
  private String name;
  
  private MessageType(final String name) {
    this.name = name;
  }
}
