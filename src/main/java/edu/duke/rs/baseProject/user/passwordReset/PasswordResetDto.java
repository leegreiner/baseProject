package edu.duke.rs.baseProject.user.passwordReset;

import java.util.UUID;

import edu.duke.rs.baseProject.validator.FieldsValueMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@FieldsValueMatch.List({
  @FieldsValueMatch(
      field = "password", 
      fieldMatch = "confirmPassword", 
      message = "{validation.passwordsMustMatch}"
    )
})
public class PasswordResetDto {
  @NonNull
  @NotNull
  private UUID passwordChangeId;
  
  @NonNull
  @NotBlank(message = "${validation.required.userName}")
  private String username;
  
  @NonNull
  @NotBlank(message = "${validation.password}")
  @Size(min = 8, max = 16, message = "${validation.password}")
  private String password;
  
  @NonNull
  @NotBlank(message = "${validation.password}")
  @Size(min = 8, max = 16, message = "${validation.password}")
  private String confirmPassword;
}
