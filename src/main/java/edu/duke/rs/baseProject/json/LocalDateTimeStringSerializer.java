package edu.duke.rs.baseProject.json;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

public class LocalDateTimeStringSerializer extends JsonSerializer<LocalDateTime> {
  private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm";
  public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_FORMAT);
  
  public LocalDateTimeStringSerializer() {
    super();
  }
  
  @Override
  public void serialize(LocalDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
    final Optional<AppPrincipal> currentUser = SecurityUtils.getPrincipal();
    ZoneId zoneId;
    
    if (currentUser.isPresent() && currentUser.get().getTimeZone() != null) {
      zoneId = currentUser.get().getTimeZone().toZoneId();
    } else {
      zoneId = ZoneId.systemDefault();
    }
    
    final ZonedDateTime systemZonedDateTime = ZonedDateTime.of(value, ZoneId.systemDefault());

    gen.writeString(FORMATTER.format(systemZonedDateTime.withZoneSameInstant(zoneId)));
  }
}
