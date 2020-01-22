package edu.duke.rs.baseProject.json;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

import com.fasterxml.jackson.databind.util.StdConverter;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.DateUtils;

public class SystemLocalDateTimeToUserLocalDateTimeConverter extends StdConverter<LocalDateTime, LocalDateTime> {
  private final SecurityUtils securityUtils;
  private final DateUtils dateUtils;
  
  public SystemLocalDateTimeToUserLocalDateTimeConverter(final SecurityUtils securityUtils) {
    this.securityUtils = securityUtils;
    this.dateUtils = new DateUtils(securityUtils);
  }
  
  public SystemLocalDateTimeToUserLocalDateTimeConverter() {
    this(new SecurityUtils());
  }
  
  @Override
  public LocalDateTime convert(final LocalDateTime value) {
    final Optional<AppPrincipal> currentUser = securityUtils.getPrincipal();
    
    if (currentUser.isEmpty()) {
      return value;
    }
    
    return dateUtils.convertToZone(value, ZoneId.systemDefault(), currentUser.get().getTimeZone().toZoneId());
  }
}
