package edu.duke.rs.baseProject.user;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto {
  private Long id;
  
  @NotBlank
  @Size(min = 8, max = 30)
  private String userName;
  
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
  private TimeZone timeZone = TimeZone.getTimeZone("UTC");

  @Default
  private boolean accountEnabled = true;
  
  private LocalDateTime lastLoggedIn;
  
  @NotEmpty
  @Default
  List<String> roles = new ArrayList<String>();
}
