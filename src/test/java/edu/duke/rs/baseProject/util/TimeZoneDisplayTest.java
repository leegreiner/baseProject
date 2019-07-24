package edu.duke.rs.baseProject.util;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.time.ZoneId;
import java.util.List;

import org.junit.Test;

public class TimeZoneDisplayTest {
  
  @Test
  public void displayTimeZoneNumEquals() {
    final List<DisplayableTimeZone> result = TimeZoneDisplay.getDisplayableTimeZones();
    
    assertThat(result.size(), equalTo(ZoneId.getAvailableZoneIds().size()));
  }
}
