package edu.duke.rs.baseProject.json;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import com.fasterxml.jackson.databind.util.StdConverter;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.DateUtils;

public class SystemLocalDateTimeToUserLocalDateTimeConverter extends StdConverter<LocalDateTime, LocalDateTime> {
  @Override
  public LocalDateTime convert(final LocalDateTime value) {
    final Optional<AppPrincipal> currentUser = SecurityUtils.getPrincipal();
    
    if (currentUser.isEmpty()) {
      return value;
    }
    
    return DateUtils.convertToZone(value, ZoneId.systemDefault(), currentUser.get().getTimeZone().toZoneId());
  }
}
