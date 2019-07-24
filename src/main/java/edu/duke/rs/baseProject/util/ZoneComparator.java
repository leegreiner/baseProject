package edu.duke.rs.baseProject.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;

public class ZoneComparator implements Comparator<ZoneId>{ 
  @Override
  public int compare(final ZoneId zoneId1, final ZoneId zoneId2) {
    final LocalDateTime now = LocalDateTime.now();
    final ZoneOffset offset1 = now.atZone(zoneId1).getOffset();
    final ZoneOffset offset2 = now.atZone(zoneId2).getOffset();
    
    return offset1.compareTo(offset2);
  }
}
