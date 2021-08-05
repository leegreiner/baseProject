package edu.duke.rs.baseProject.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

public class TimeZoneDisplay {
  private static final ZoneId UTC_ZONE_ID = TimeZone.getTimeZone("UTC").toZoneId();
  
  public static List<DisplayableTimeZone> getDisplayableTimeZones(){
    final LocalDateTime now = LocalDateTime.now();
    
    return ZoneId.getAvailableZoneIds().stream()
      .map(ZoneId::of)
      .sorted(new ZoneComparator())
      .map(id -> new DisplayableTimeZone(String.format("(%s%s) %s", UTC_ZONE_ID, getOffset(now, id), id.getId()), id.getId()))
      .collect(Collectors.toList());
  }
  
  private static String getOffset(LocalDateTime dateTime, ZoneId id) {
    return dateTime
      .atZone(id)
      .getOffset()
      .getId()
      .replace("Z", "+00:00");
  }
}
