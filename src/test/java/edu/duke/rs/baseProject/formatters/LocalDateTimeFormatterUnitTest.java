package edu.duke.rs.baseProject.formatters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.DateUtils;

@RunWith(PowerMockRunner.class)
@PowerMockIgnore({"com.sun.org.apache.xerces.*", "javax.xml.*", "org.xml.*"})
@PrepareForTest(SecurityUtils.class)
public class LocalDateTimeFormatterUnitTest {
  private static final ZoneId ZONE_ID = ZoneId.of("Africa/Accra");

  @Mock
  private AppPrincipal appPrincipal;
  final LocalDateTimeFormatter formatter = new LocalDateTimeFormatter();
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    mockStatic(SecurityUtils.class);
  }
  
  @Test
  public void whenNoLocalDateTimePassed_thenEmptyStringReturned() {
    assertThat("", equalTo(formatter.print(null, Locale.getDefault()))); 
  }
  
  @Test
  public void whenAppUserNotFound_thenSystemTimezoneUsed() {
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    final LocalDateTime now = LocalDateTime.now();
    final String actual = formatter.print(now, Locale.getDefault());
    
    final ZonedDateTime systemZonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
    
    final String expected = DateUtils.DEFAULT_DATE_TIME_FORMATTER.format(systemZonedDateTime.withZoneSameInstant(ZoneId.systemDefault()));
    
    assertThat(actual, equalTo(expected));
  }
  
  @Test
  public void whenAppUserFound_thenAppUserTimezoneUsed() {
    final TimeZone timeZone = TimeZone.getTimeZone(ZONE_ID);
    when(appPrincipal.getTimeZone()).thenReturn(timeZone);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    final LocalDateTime now = LocalDateTime.now();
    final String actual = formatter.print(now, Locale.getDefault());
    
    final ZonedDateTime systemZonedDateTime = ZonedDateTime.of(now, ZoneId.systemDefault());
    
    final String expected = DateUtils.DEFAULT_DATE_TIME_FORMATTER.format(systemZonedDateTime.withZoneSameInstant(ZONE_ID));
    
    assertThat(actual, equalTo(expected));
  }
  
  @Test
  public void whenBlankTextPassed_thenEmptyStringReturned() throws ParseException {
    assertThat(formatter.parse("", Locale.getDefault()), nullValue());
  }
  
  @Test
  public void whenCurrentUserPresent_thenDateConvertedFromUsersTimezone() throws ParseException {
    final TimeZone timeZone = TimeZone.getTimeZone(ZONE_ID);
    final LocalDateTime date = LocalDateTime.of(2019, 1, 1, 12, 30);
    final LocalDateTime currentUserTime = DateUtils.convertToZone(date, ZoneId.systemDefault(), ZONE_ID);
    final String currentUserTimeString = String.format("%d-%02d-%02d %02d:%02d", currentUserTime.getYear(),
        currentUserTime.getMonth().getValue(), currentUserTime.getDayOfMonth(),currentUserTime.getHour(),
        currentUserTime.getMinute());
    
    when(appPrincipal.getTimeZone()).thenReturn(timeZone);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    final LocalDateTime actual = this.formatter.parse(currentUserTimeString, Locale.getDefault());
    
    assertThat(actual, equalTo(date));
  }
  
  @Test
  public void whenCurrentUserNotPresent_thenDateConvertedFromSystemTimezone() throws ParseException {
    final LocalDateTime date = LocalDateTime.of(2019, 1, 1, 12, 30);
    final String dateString = String.format("%d-%02d-%02d %02d:%02d", date.getYear(),
        date.getMonth().getValue(), date.getDayOfMonth(),date.getHour(),
        date.getMinute());
    
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    final LocalDateTime actual = this.formatter.parse(dateString, Locale.getDefault());
    
    assertThat(actual, equalTo(date));
  }
  
  @Test(expected = DateTimeParseException.class)
  public void whenParseCalled_thenNotImplementedExceptionThrown() throws ParseException {
    formatter.parse("abc", Locale.getDefault());
  }
}
