package edu.duke.rs.baseProject.user;

import java.util.TimeZone;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NonNull;

@Data
public class UserProfile {
  @NotNull
  @NonNull
  private TimeZone timeZone;
}
