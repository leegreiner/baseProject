package edu.duke.rs.baseProject.formatters;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.Formatter;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.DateUtils;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
  @Override
  public String print(LocalDateTime dateTime, Locale locale) {
    if (dateTime == null) {
      return "";
    }
    
    final Optional<AppPrincipal> currentUser = SecurityUtils.getPrincipal();
    ZoneId zoneId;
    
    if (currentUser.isPresent()) {
      zoneId = currentUser.get().getTimeZone().toZoneId();
    } else {
      zoneId = ZoneId.systemDefault();
    }
    
    final ZonedDateTime systemZonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
    
    return DateUtils.DEFAULT_DATE_TIME_FORMATTER.format(systemZonedDateTime.withZoneSameInstant(zoneId));
  }

  @Override
  public LocalDateTime parse(String text, Locale locale) throws ParseException {
    if (StringUtils.isBlank(text)) {
      return null;
    }
    
    final Optional<AppPrincipal> currentUser = SecurityUtils.getPrincipal();
    ZoneId zoneId;
    
    if (currentUser.isPresent()) {
      zoneId = currentUser.get().getTimeZone().toZoneId();
    } else {
      zoneId = ZoneId.systemDefault();
    }
    
    return DateUtils.convertToZone(LocalDateTime.parse(text, DateUtils.DEFAULT_DATE_TIME_FORMATTER), zoneId, ZoneId.systemDefault());
  }
}
