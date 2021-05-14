package edu.duke.rs.baseProject.converter;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public abstract class AbstractEnumConverter<T extends Enum<T> & PersistableEnum<E>, E>
    implements AttributeConverter<T, E> {
  private final Class<T> clazz;

  public AbstractEnumConverter(final Class<T> clazz) {
    this.clazz = clazz;
  }

  @Override
  public E convertToDatabaseColumn(final T attribute) {
    return attribute != null ? attribute.getValue() : null;
  }

  @Override
  public T convertToEntityAttribute(final E dbData) {
    final T[] enums = clazz.getEnumConstants();

    for (final T e : enums) {
      if (e.getValue().equals(dbData)) {
        return e;
      }
    }

    throw new UnsupportedOperationException("Unknown enumeration " + dbData.toString());
  }
}