package edu.duke.rs.baseProject.formatters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.Optional;
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

@RunWith(PowerMockRunner.class)
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
    
    final String expected = LocalDateTimeFormatter.FORMATTER.format(systemZonedDateTime.withZoneSameInstant(ZoneId.systemDefault()));
    
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
    
    final String expected = LocalDateTimeFormatter.FORMATTER.format(systemZonedDateTime.withZoneSameInstant(ZONE_ID));
    
    assertThat(actual, equalTo(expected));
  }
  
  @Test(expected = ParseException.class)
  public void whenParseCalled_thenNotImplementedExceptionThrown() throws ParseException {
    formatter.parse("abc", Locale.getDefault());
  }
}
