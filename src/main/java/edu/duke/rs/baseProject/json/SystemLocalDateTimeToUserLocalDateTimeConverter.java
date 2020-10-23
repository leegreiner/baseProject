package edu.duke.rs.baseProject.json;

import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.databind.util.StdConverter;

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
    return securityUtils.getPrincipal()
        .map(appPrincipal -> dateUtils.convertToZone(value, ZoneId.systemDefault(), appPrincipal.getTimeZone().toZoneId()))
        .orElseGet(() -> value);
  }
}
