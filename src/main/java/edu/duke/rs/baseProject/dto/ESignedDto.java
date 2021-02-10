package edu.duke.rs.baseProject.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class ESignedDto {
  public interface CreateChecks {}
  public interface UpdateChecks {}
  public interface DeleteChecks {}
  
  @NotBlank(groups = {UpdateChecks.class, DeleteChecks.class})
  @Size(max = 1000, groups = UpdateChecks.class)
  private String changeReason;
  
  @NotBlank(groups = {CreateChecks.class, UpdateChecks.class, DeleteChecks.class})
  @Size(min = 8, max = 200, groups = UpdateChecks.class)
  private String password;
}
