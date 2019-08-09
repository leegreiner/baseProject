package edu.duke.rs.baseProject.formatters;

import java.text.ParseException;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang.WordUtils;
import org.springframework.format.Formatter;

import edu.duke.rs.baseProject.role.Role;

public class RolesFormatter implements Formatter<Set<Role>> {
  @Override
  public String print(Set<Role> roles, Locale locale) {
    if (roles == null || roles.size() == 0) {
      return "";
    }
    
    return roles.stream()
        .map(role -> WordUtils.capitalize(role.getName().name().toLowerCase()))
        .collect(Collectors.joining(", "));
  }

  @Override
  public Set<Role> parse(String text, Locale locale) throws ParseException {
    throw new ParseException("Formating not supported", 0);
  }
}
