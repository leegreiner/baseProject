package edu.duke.rs.baseProject.user;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import edu.duke.rs.baseProject.dto.ESignedDto;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class UserDto extends ESignedDto {
  @NotNull(groups = {UpdateChecks.class})
  private UUID id;
  
  @Builder
  public UserDto(final UUID id, final String username, final String firstName, final String middleInitial,
      final String lastName, final String email, final TimeZone timeZone, final boolean accountEnabled,
      final List<String> roles, final String changeReason, final String password) {
    super(changeReason, password);
    this.id = id;
    this.username = username;
    this.firstName = firstName;
    this.middleInitial = middleInitial;
    this.lastName = lastName;
    this.email = email;
    this.timeZone = timeZone;
    this.accountEnabled = accountEnabled;
    
    if (roles != null) {
      this.roles.addAll(roles);
    }
  }
  
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
  
  private TimeZone timeZone = TimeZone.getTimeZone("UTC");

  private boolean accountEnabled = true;
  
  @NotEmpty
  private List<String> roles = new ArrayList<String>();
}
