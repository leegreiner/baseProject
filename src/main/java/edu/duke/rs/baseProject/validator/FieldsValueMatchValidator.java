package edu.duke.rs.baseProject.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.BeanWrapperImpl;

public class FieldsValueMatchValidator implements ConstraintValidator<FieldsValueMatch, Object> {
  private String field;
  private String fieldMatch;

  public void initialize(final FieldsValueMatch constraintAnnotation) {
      this.field = constraintAnnotation.field();
      this.fieldMatch = constraintAnnotation.fieldMatch();
  }

  @Override
  public boolean isValid(final Object value, 
    final ConstraintValidatorContext context) {

    final Object fieldValue = new BeanWrapperImpl(value)
      .getPropertyValue(field);
    final Object fieldMatchValue = new BeanWrapperImpl(value)
      .getPropertyValue(fieldMatch);
     
    if (fieldValue != null) {
        return fieldValue.equals(fieldMatchValue);
    } else {
        return fieldMatchValue == null;
    }
  }
}
