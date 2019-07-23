package edu.duke.rs.baseProject.util;

import lombok.Data;
import lombok.NonNull;

@Data
public class DisplayableTimeZone {
  
  @NonNull
  private String label;
  
  @NonNull
  private String zoneId;
 
}
