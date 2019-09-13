package edu.duke.rs.baseProject.role;

import java.text.ParseException;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.format.Formatter;

public class RolesFormatter implements Formatter<Set<Role>> {
  @Override
  public String print(Set<Role> roles, Locale locale) {
    if (roles == null || roles.size() == 0) {
      return "";
    }
    
    return roles.stream()
      .sorted((r1, r2) -> r1.getName().getValue().compareTo(r2.getName().getValue()))
      .collect(Collectors.toList())
      .stream()
        .map(role -> role.getName().getValue())
        .collect(Collectors.joining(", "));
  }

  @Override
  public Set<Role> parse(String text, Locale locale) throws ParseException {
    throw new ParseException("Formating not supported", 0);
  }
}
