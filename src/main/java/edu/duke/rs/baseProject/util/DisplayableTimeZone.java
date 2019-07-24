package edu.duke.rs.baseProject.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class DisplayableTimeZone {
  @NonNull
  private String label;
  
  @NonNull
  private String zoneId;
}
