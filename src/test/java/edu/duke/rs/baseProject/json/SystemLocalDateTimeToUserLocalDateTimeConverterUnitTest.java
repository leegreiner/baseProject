package edu.duke.rs.baseProject.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.util.DateUtils;

public class SystemLocalDateTimeToUserLocalDateTimeConverterUnitTest {
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private SecurityUtils securityUtils;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void whenCurrentUserPresent_thenDateConvertedToUsersTimezone() throws Exception {
    final ZoneId zoneId = ZoneId.of("America/Los_Angeles");
    final TimeZone timeZone = TimeZone.getTimeZone(zoneId);
    when(appPrincipal.getTimeZone()).thenReturn(timeZone);
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    final SystemLocalDateTimeToUserLocalDateTimeConverter converter = new SystemLocalDateTimeToUserLocalDateTimeConverter(securityUtils);
    final LocalDateTime now = LocalDateTime.now();
    
    final LocalDateTime actual = converter.convert(now);
    final LocalDateTime expected = new DateUtils().convertToZone(now, ZoneId.systemDefault(), zoneId);
    
    assertThat(actual, equalTo(expected));
  }
  
  @Test
  public void whenCurrentUserNotPresent_thenDateConvertedToSystemTimezone() throws Exception {
    when(securityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    final SystemLocalDateTimeToUserLocalDateTimeConverter converter = new SystemLocalDateTimeToUserLocalDateTimeConverter(securityUtils);
    final LocalDateTime now = LocalDateTime.now();
    
    final LocalDateTime actual = converter.convert(now);
    
    assertThat(actual, equalTo(now));
  }
}
