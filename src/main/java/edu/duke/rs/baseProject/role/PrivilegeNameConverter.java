package edu.duke.rs.baseProject.role;

import java.util.stream.Stream;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class PrivilegeNameConverter implements AttributeConverter<PrivilegeName, String> {
    @Override
    public String convertToDatabaseColumn(final PrivilegeName privilegeName) {
      return privilegeName == null ? null : privilegeName.getValue();
    }

    @Override
    public PrivilegeName convertToEntityAttribute(final String name) {
      if (name == null) {
        return null;
      }
      
      return Stream.of(PrivilegeName.values())
          .filter(r -> r.getValue().equals(name))
          .findFirst()
          .orElseThrow(() -> new IllegalArgumentException("Unknown privilege name " + name));
    }
  }
