package edu.duke.rs.baseProject.security.password;

public interface PasswordValidator {
  boolean isValid(String password);
  boolean isSame(String password, String encryptedPassword);
}
