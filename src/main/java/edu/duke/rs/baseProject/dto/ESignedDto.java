package edu.duke.rs.baseProject.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import edu.duke.rs.baseProject.validator.CurrentUsersPassword;
import edu.duke.rs.baseProject.validator.groups.Create;
import edu.duke.rs.baseProject.validator.groups.Delete;
import edu.duke.rs.baseProject.validator.groups.Update;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@ToString
public abstract class ESignedDto {
  @NotBlank(groups = {Update.class, Delete.class})
  @Size(max = 1000, groups = {Update.class, Delete.class})
  private String reasonForChange;
  
  @NotBlank(groups = {Create.class, Update.class, Delete.class})
  @CurrentUsersPassword(groups = {Create.class, Update.class, Delete.class})
  private String password;
}
