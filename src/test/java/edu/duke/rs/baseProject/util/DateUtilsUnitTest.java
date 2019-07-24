package edu.duke.rs.baseProject.util;

import static org.mockito.Mockito.when;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
public class DateUtilsUnitTest {
  private static final ZoneId ZONE_ID = ZoneId.of("Africa/Accra");
  @Mock
  private AppPrincipal appPrincipal;

  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    mockStatic(SecurityUtils.class);
  }
  
  @Test
  public void whenCurrentUserExists_thenConvertLocalDateTimeToCurrentUserTimeConverts() {
    final TimeZone timeZone = TimeZone.getTimeZone(ZONE_ID);
    final LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
    when(appPrincipal.getTimeZone()).thenReturn(timeZone);
    when(SecurityUtils.getPrincipal()).thenReturn(appPrincipal);
    
    final LocalDateTimes ldt = new LocalDateTimes(now, now.plusDays(1L));
    
    ZonedDateTime from = ZonedDateTime.of(ldt.getStart(), ZoneId.systemDefault());
    ZonedDateTime to = from.withZoneSameInstant(ZONE_ID);
    final LocalDateTime expectedStart = to.toLocalDateTime();
    from = ZonedDateTime.of(ldt.getEnd(), ZoneId.systemDefault());
    to = from.withZoneSameInstant(ZONE_ID);
    final LocalDateTime expectedEnd = to.toLocalDateTime();
    
    DateUtils.convertLocalDateTimeToCurrentUserTime(ldt);
    
    assertThat(expectedStart, equalTo(ldt.getStart()));
    assertThat(expectedEnd, equalTo(ldt.getEnd()));
  }
  
  @Test
  public void whenCurrentUserDoesntExists_thenConvertLocalDateTimeToCurrentUserTimeDoesntConvert() {
    final LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
    final LocalDateTimes ldt = new LocalDateTimes(now, now.plusDays(1L));
    when(SecurityUtils.getPrincipal()).thenReturn(null);
    
    DateUtils.convertLocalDateTimeToCurrentUserTime(ldt);
    
    assertThat(ldt.getStart(), equalTo(ldt.getStart()));
    assertThat(ldt.getEnd(), equalTo(ldt.getEnd()));
  }
  
  @Test
  public void whenCurrentUserExists_thenConvertCurrentUserTimeToLocalDateTimeConverts() {
    final TimeZone timeZone = TimeZone.getTimeZone(ZONE_ID);
    final LocalDateTime now = LocalDateTime.now(ZONE_ID);
    when(appPrincipal.getTimeZone()).thenReturn(timeZone);
    when(SecurityUtils.getPrincipal()).thenReturn(appPrincipal);
    
    final LocalDateTimes ldt = new LocalDateTimes(now, now.plusDays(1L));
    
    ZonedDateTime from = ZonedDateTime.of(ldt.getStart(), ZONE_ID);
    ZonedDateTime to = from.withZoneSameInstant(ZoneId.systemDefault());
    final LocalDateTime expectedStart = to.toLocalDateTime();
    from = ZonedDateTime.of(ldt.getEnd(), ZONE_ID);
    to = from.withZoneSameInstant(ZoneId.systemDefault());
    final LocalDateTime expectedEnd = to.toLocalDateTime();
    
    DateUtils.convertCurrentUserTimeToLocalDateTime(ldt);
    
    assertThat(expectedStart, equalTo(ldt.getStart()));
    assertThat(expectedEnd, equalTo(ldt.getEnd()));
  }
  
  @Test
  public void whenCurrentUserDoesntExists_thenConvertCurrentUserTimeToLocalDateTimeDoesntConvert() {
    final LocalDateTime now = LocalDateTime.now(ZONE_ID);
    final LocalDateTimes ldt = new LocalDateTimes(now, now.plusDays(1L));
    when(SecurityUtils.getPrincipal()).thenReturn(null);
    
    DateUtils.convertCurrentUserTimeToLocalDateTime(ldt);
    
    assertThat(ldt.getStart(), equalTo(ldt.getStart()));
    assertThat(ldt.getEnd(), equalTo(ldt.getEnd()));
  }
  
  @Test
  public void whenDifferentZonesPassed_thenLocalDateTimesConvertedToNewZone() {
    final ZoneId fromZoneId = ZoneId.of("America/New_York");
    final ZoneId toZoneId = ZoneId.of("America/Los_Angeles");
    final LocalDateTime now = LocalDateTime.now();
    
    final ZonedDateTime from = ZonedDateTime.of(now, fromZoneId);
    final ZonedDateTime to = from.withZoneSameInstant(toZoneId);
    final LocalDateTime expectedLocalDateTime = to.toLocalDateTime();
    
    final LocalDateTime result = DateUtils.convertToZone(now, fromZoneId, toZoneId);
    
    assertThat(expectedLocalDateTime, equalTo(result));
  }
  
  @Test
  public void whenSameZonesPassed_thenLocalDateTimesConvertedToNewZone() {
    final ZoneId fromZoneId = ZoneId.of("America/Los_Angeles");
    final ZoneId toZoneId = fromZoneId;
    final LocalDateTime now = LocalDateTime.now();
    
    final ZonedDateTime from = ZonedDateTime.of(now, fromZoneId);
    final ZonedDateTime to = from.withZoneSameInstant(toZoneId);
    final LocalDateTime expectedLocalDateTime = to.toLocalDateTime();
    
    final LocalDateTime result = DateUtils.convertToZone(now, fromZoneId, toZoneId);
    
    assertThat(expectedLocalDateTime, equalTo(result));
  }
  
  @Getter
  @Setter
  @AllArgsConstructor
  @NoArgsConstructor
  @ToString
  class LocalDateTimes {
    private LocalDateTime start;
    private LocalDateTime end;
  }
}
