package edu.duke.rs.baseProject.user.passwordReset;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class InitiatePasswordResetDto {
  @NonNull
  @NotBlank(message = "${validation.email}")
  @Email(message = "${validation.email}")
  private String email;
}
