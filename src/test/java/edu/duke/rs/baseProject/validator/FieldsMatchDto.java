package edu.duke.rs.baseProject.validator;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@FieldsValueMatch.List({
  @FieldsValueMatch(
      field = "field1", 
      fieldMatch = "field2", 
      message = "{validation.passwordsMustMatch}"
    )
})
public class FieldsMatchDto {
  private String field1;
  private String field2;
}
