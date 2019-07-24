package edu.duke.rs.baseProject.json;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

public class LocalDateTimeStringSerializer extends JsonSerializer<LocalDateTime> {
  private static final String DATE_FORMAT = ("MM/dd/yyyy HH:mm z");
  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
  
  public LocalDateTimeStringSerializer() {
    super();
  }
  
  @Override
  public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    final AppPrincipal currentUser = SecurityUtils.getPrincipal();
    ZoneId zoneId;
    
    if (currentUser == null || currentUser.getTimeZone() == null) {
      zoneId = ZoneId.systemDefault();
    } else {
      zoneId = currentUser.getTimeZone().toZoneId();
    }
    
    final ZonedDateTime systemZonedDateTime = ZonedDateTime.of(value, ZoneId.systemDefault());

    gen.writeString(FORMATTER.format(systemZonedDateTime.withZoneSameInstant(zoneId)));
  }
}
