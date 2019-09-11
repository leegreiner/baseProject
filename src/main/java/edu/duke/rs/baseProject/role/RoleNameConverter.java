package edu.duke.rs.baseProject.role;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class RoleNameConverter implements AttributeConverter<RoleName, String> {
  @Override
  public String convertToDatabaseColumn(final RoleName roleName) {
    return roleName == null ? null : roleName.getName();
  }

  @Override
  public RoleName convertToEntityAttribute(final String name) {
    if (name == null) {
      return null;
    }
    
    return Stream.of(RoleName.values())
        .filter(r -> r.getName().equals(name))
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Unknown role name " + name));
  }
}
