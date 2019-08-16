package edu.duke.rs.baseProject.security.password;

import org.springframework.security.crypto.password.PasswordEncoder;

class NoOpPasswordEncoder implements PasswordEncoder {
  @Override
  public String encode(CharSequence rawPassword) {
    return rawPassword.toString();
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    return rawPassword.equals(encodedPassword);
  }
}
