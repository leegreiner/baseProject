package edu.duke.rs.baseProject.validator;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class FieldsValueMatchValidatorUnitTest {
  private static Validator validator;

  @BeforeAll
  public static void setUp() {
      ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
      validator = factory.getValidator();
  }
  
  @Test
  public void whenFieldsMatch_thenValidatorReturnsNoViolations() {
    final FieldsMatchDto dto = new FieldsMatchDto();
    dto.setField1("a test");
    dto.setField2(dto.getField1());
    
    Set<ConstraintViolation<FieldsMatchDto>> constraintViolations = validator.validate(dto);
    
    assertThat(constraintViolations.size(), equalTo(0));
  }
  
  @Test
  public void whenBothFieldsAreNull_thenValidatorReturnsNoViolations() {
    Set<ConstraintViolation<FieldsMatchDto>> constraintViolations =
        validator.validate(new FieldsMatchDto());
    
    assertThat(constraintViolations.size(), equalTo(0));
  }
  
  @Test
  public void whenFieldsDoNotMatch_thenValidatorReturnsViolations() {
    final FieldsMatchDto dto = new FieldsMatchDto();
    dto.setField1("a test");
    dto.setField2(dto.getField1() + "m");
    
    Set<ConstraintViolation<FieldsMatchDto>> constraintViolations = validator.validate(dto);
    
    assertThat(constraintViolations.size(), equalTo(1));
    assertThat(constraintViolations.iterator().next().getMessage(), equalTo("{validation.passwordsMustMatch}"));
  }
  
  @Test
  public void whenFirstFieldNullAndSecondFieldNotNull_thenValidatorReturnsViolations() {
    final FieldsMatchDto dto = new FieldsMatchDto();
    dto.setField2("a test");
    
    Set<ConstraintViolation<FieldsMatchDto>> constraintViolations = validator.validate(dto);
    
    assertThat(constraintViolations.size(), equalTo(1));
    assertThat(constraintViolations.iterator().next().getMessage(), equalTo("{validation.passwordsMustMatch}"));
  }
}
