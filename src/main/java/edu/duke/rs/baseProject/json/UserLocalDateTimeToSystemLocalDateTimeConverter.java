package edu.duke.rs.baseProject.json;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import com.fasterxml.jackson.databind.util.StdConverter;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.DateUtils;

public class UserLocalDateTimeToSystemLocalDateTimeConverter extends StdConverter<LocalDateTime, LocalDateTime> {
  @Override
  public LocalDateTime convert(LocalDateTime value) {
    final Optional<AppPrincipal> currentUser = SecurityUtils.getPrincipal();
    
    if (currentUser.isEmpty()) {
      return value;
    }
    
    return DateUtils.convertToZone(value, currentUser.get().getTimeZone().toZoneId(), ZoneId.systemDefault());
  }
}
