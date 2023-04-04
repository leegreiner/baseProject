package edu.duke.rs.baseProject.validator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Constraint(validatedBy = CurrentUsersPasswordValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface CurrentUsersPassword {
  String message() default "{validation.currentUsersPassword.invalid}";
  Class<?>[] groups() default {};
  Class<? extends Payload>[] payload() default {};
}
