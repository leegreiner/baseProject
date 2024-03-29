package edu.duke.rs.baseProject.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import edu.duke.rs.baseProject.dto.ESignedDto;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.validator.groups.Update;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder.Default;
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
public class UserDto extends ESignedDto {
  @NotNull(groups = {Update.class})
  private UUID id;

  @NotBlank
  @Size(min = 4, max = 30)
  private String username;
  
  @NotBlank
  @Size(max = 30)
  private String firstName;
  
  @Size(max = 1)
  private String middleInitial;
  
  @NotBlank
  @Size(max = 30)
  private String lastName;
  
  @NotBlank
  @Size(max = 320)
  private String email;
  
  @Default
  @NotNull(message = "{validation.timeZone}")
  private TimeZone timeZone = TimeZone.getTimeZone("UTC");

  @Default
  private boolean accountEnabled = true;
  
  private LocalDateTime lastLoggedIn;
  
  @NotEmpty(message = "{validation.roles}")
  @Default
  List<RoleName> roles = new ArrayList<RoleName>();
}
