package edu.duke.rs.baseProject.formatters;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.springframework.format.Formatter;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

public class LocalDateTimeFormatter implements Formatter<LocalDateTime> {
  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd/MMM/yyyy HH:mm z");
  
  @Override
  public String print(LocalDateTime dateTime, Locale locale) {
    if (dateTime == null) {
      return "";
    }
    
    final AppPrincipal currentUser = SecurityUtils.getPrincipal();
    ZoneId zoneId;
    
    if (currentUser == null || currentUser.getTimeZone() == null) {
      zoneId = ZoneId.systemDefault();
    } else {
      zoneId = currentUser.getTimeZone().toZoneId();
    }
    
    final ZonedDateTime systemZonedDateTime = ZonedDateTime.of(dateTime, ZoneId.systemDefault());
    
    return FORMATTER.format(systemZonedDateTime.withZoneSameInstant(zoneId));
  }

  @Override
  public LocalDateTime parse(String text, Locale locale) throws ParseException {
    throw new ParseException("Formating not supported", 0);
  }
}
