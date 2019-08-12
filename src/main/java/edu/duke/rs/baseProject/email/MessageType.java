package edu.duke.rs.baseProject.email;

import lombok.Getter;

@Getter
public enum MessageType {
  TEST("testEmail");
  
  private String name;
  
  private MessageType(final String name) {
    this.name = name;
  }
}
