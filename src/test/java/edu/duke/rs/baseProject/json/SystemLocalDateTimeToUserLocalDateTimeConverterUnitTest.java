package edu.duke.rs.baseProject.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.time.LocalDateTime;
import java.time.ZoneId;
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
import edu.duke.rs.baseProject.util.DateUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
public class SystemLocalDateTimeToUserLocalDateTimeConverterUnitTest {
  @Mock
  private AppPrincipal appPrincipal;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    mockStatic(SecurityUtils.class);
  }
  
  @Test
  public void whenCurrentUserPresent_thenDateConvertedToUsersTimezone() throws Exception {
    final ZoneId zoneId = ZoneId.of("America/Los_Angeles");
    final TimeZone timeZone = TimeZone.getTimeZone(zoneId);
    when(appPrincipal.getTimeZone()).thenReturn(timeZone);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    final SystemLocalDateTimeToUserLocalDateTimeConverter converter = new SystemLocalDateTimeToUserLocalDateTimeConverter();
    final LocalDateTime now = LocalDateTime.now();
    
    final LocalDateTime actual = converter.convert(now);
    final LocalDateTime expected = DateUtils.convertToZone(now, ZoneId.systemDefault(), zoneId);
    
    assertThat(actual, equalTo(expected));
  }
  
  @Test
  public void whenCurrentUserNotPresent_thenDateConvertedToSystemTimezone() throws Exception {
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    final SystemLocalDateTimeToUserLocalDateTimeConverter converter = new SystemLocalDateTimeToUserLocalDateTimeConverter();
    final LocalDateTime now = LocalDateTime.now();
    
    final LocalDateTime actual = converter.convert(now);
    
    assertThat(actual, equalTo(now));
  }
}
