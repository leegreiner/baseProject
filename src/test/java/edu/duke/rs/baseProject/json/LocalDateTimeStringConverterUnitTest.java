package edu.duke.rs.baseProject.json;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.TimeZone;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.fasterxml.jackson.core.JsonGenerator;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
public class LocalDateTimeStringConverterUnitTest {
  @Mock
  private JsonGenerator jsonGenerator;
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
    
    final LocalDateTimeStringSerializer serializer = new LocalDateTimeStringSerializer();
    final LocalDateTime now = LocalDateTime.now();
    
    serializer.serialize(now, jsonGenerator, null);
    
    final ArgumentCaptor<String> serializedDateTime = ArgumentCaptor.forClass(String.class);
    
    verify(jsonGenerator).writeString(serializedDateTime.capture());
    
    final String expected = LocalDateTimeStringSerializer.FORMATTER.format(ZonedDateTime.of(now, ZoneId.systemDefault()).withZoneSameInstant(zoneId));
    
    assertThat(serializedDateTime.getValue(), equalTo(expected));
  }
  
  @Test
  public void whenCurrentUserNotPresent_thenDateConvertedToSystemTimezone() throws Exception {
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    final LocalDateTimeStringSerializer serializer = new LocalDateTimeStringSerializer();
    final LocalDateTime now = LocalDateTime.now();
    
    serializer.serialize(now, jsonGenerator, null);
    
    final ArgumentCaptor<String> serializedDateTime = ArgumentCaptor.forClass(String.class);
    
    verify(jsonGenerator).writeString(serializedDateTime.capture());
    
    final String expected = LocalDateTimeStringSerializer.FORMATTER.format(ZonedDateTime.of(now, ZoneId.systemDefault()));
    
    assertThat(serializedDateTime.getValue(), equalTo(expected));
  }
  
  @Test
  public void whenCurrentUserTimezoneNotPresent_thenDateConvertedToSystemTimezone() throws Exception {
    when(appPrincipal.getTimeZone()).thenReturn(null);
    when(SecurityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    
    final LocalDateTimeStringSerializer serializer = new LocalDateTimeStringSerializer();
    final LocalDateTime now = LocalDateTime.now();
    
    serializer.serialize(now, jsonGenerator, null);
    
    final ArgumentCaptor<String> serializedDateTime = ArgumentCaptor.forClass(String.class);
    
    verify(jsonGenerator).writeString(serializedDateTime.capture());
    
    final String expected = LocalDateTimeStringSerializer.FORMATTER.format(ZonedDateTime.of(now, ZoneId.systemDefault()));
    
    assertThat(serializedDateTime.getValue(), equalTo(expected));
  }

}
