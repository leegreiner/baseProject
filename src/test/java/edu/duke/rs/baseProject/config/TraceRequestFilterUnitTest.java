package edu.duke.rs.baseProject.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.trace.http.HttpExchangeTracer;
import org.springframework.boot.actuate.trace.http.HttpTraceRepository;

public class TraceRequestFilterUnitTest {
  @Mock
  private HttpTraceRepository traceRepository;
  @Mock
  private HttpExchangeTracer tracer;
  @Mock
  private HttpServletRequest request;
  private TraceRequestFilter filter;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    filter = new TraceRequestFilter(traceRepository, tracer);
  }
  
  @Test
  public void whenServletPathContainsStringToIgnore_thenPathSouldNotBeFiltered() throws Exception {
    List<String> toBeIgnored = List.of("/css/", "/js/", "/img/", "/webfonts/");
    
    for (String ignored : toBeIgnored) {
      when(request.getServletPath()).thenReturn("http://www.mine.org " + ignored + "abc");
      
      assertThat(filter.shouldNotFilter(request)).isEqualTo(true);
    }
  }
  
  @Test
  public void whenServletPathDoesNotContainStringToIgnore_thenPathSouldBeFiltered() throws Exception {
    List<String> toBeIgnored = List.of("/acss/", "/ajs/", "/aimg/", "/awebfonts/");
    
    for (String ignored : toBeIgnored) {
      when(request.getServletPath()).thenReturn("http://www.mine.org " + ignored + "abc");
      
      assertThat(filter.shouldNotFilter(request)).isEqualTo(false);
    }
  }
}
