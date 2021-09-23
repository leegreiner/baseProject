package edu.duke.rs.baseProject.security;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jeasy.random.randomizers.Ipv4AddressRandomizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import edu.duke.rs.baseProject.AbstractBaseTest;

public class AuthenticationAttemptListenerUnitTest extends AbstractBaseTest {
  private List<String> ignoredPrincipals = List.of("anonymousUser", "mgmt_user");
  private static final String DETAILS = "details";
  @Mock
  private AuditApplicationEvent auditApplicationEvent;
  @Mock
  private AuditEvent auditEvent;
  @Mock
  private WebAuthenticationDetails webAuthenticationDetails;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenIgnoredPrincipalEvent_thenNothingIsLogged() {
    for (final String ignoredPrincipal : ignoredPrincipals) {
      when(auditApplicationEvent.getAuditEvent()).thenReturn(auditEvent);
      when(auditEvent.getPrincipal()).thenReturn(ignoredPrincipal);
      
      final AuthenticationAttemptListener listener = new AuthenticationAttemptListener(ignoredPrincipal);
      
      listener.authenticationAttempted(auditApplicationEvent);
      
      verify(auditApplicationEvent, times(1)).getAuditEvent();
      verify(auditEvent, times(1)).getPrincipal();
      verifyNoMoreInteractions(auditApplicationEvent);
      verifyNoMoreInteractions(auditEvent);
      
      reset(auditApplicationEvent);
      reset(auditEvent);
    }
  }
  
  @Test
  public void whenNonIgnoredPrincipalEvent_thenEventIsLogged() {
    final Map<String, Object> data = new HashMap<String, Object>();
    data.put(DETAILS, webAuthenticationDetails);
    
    when(auditApplicationEvent.getAuditEvent()).thenReturn(auditEvent);
    when(auditEvent.getPrincipal()).thenReturn(easyRandom.nextObject(String.class));
    when(auditEvent.getType()).thenReturn(easyRandom.nextObject(String.class));
    when(auditEvent.getData()).thenReturn(data);
    when(webAuthenticationDetails.getRemoteAddress()).thenReturn(new Ipv4AddressRandomizer(new Random().nextLong()).getRandomValue());
    when(webAuthenticationDetails.getSessionId()).thenReturn("abc123");
    
    final AuthenticationAttemptListener listener = new AuthenticationAttemptListener(easyRandom.nextObject(String.class));
    
    listener.authenticationAttempted(auditApplicationEvent);
    
    verify(auditApplicationEvent, times(1)).getAuditEvent();
    verify(auditEvent, times(2)).getPrincipal();
    verify(auditEvent, times(1)).getType();
    verify(auditEvent, times(1)).getData();
    verify(webAuthenticationDetails, times(1)).getRemoteAddress();
    verify(webAuthenticationDetails, times(1)).getSessionId();
    verifyNoMoreInteractions(auditApplicationEvent);
    verifyNoMoreInteractions(auditEvent);
    verifyNoMoreInteractions(webAuthenticationDetails);
    
    reset(auditApplicationEvent);
    reset(auditEvent);
    reset(webAuthenticationDetails);
  }
  
  @Test
  public void whenNonIgnoredPrincipalEventWithNoDetails_thenEventIsLogged() {
    when(auditApplicationEvent.getAuditEvent()).thenReturn(auditEvent);
    when(auditEvent.getPrincipal()).thenReturn(easyRandom.nextObject(String.class));
    when(auditEvent.getType()).thenReturn("type1");
    when(auditEvent.getData()).thenReturn(new HashMap<String, Object>());
    when(webAuthenticationDetails.getRemoteAddress()).thenReturn((new Ipv4AddressRandomizer(new Random().nextLong()).getRandomValue()));
    when(webAuthenticationDetails.getSessionId()).thenReturn(easyRandom.nextObject(String.class));
    
    final AuthenticationAttemptListener listener = new AuthenticationAttemptListener(easyRandom.nextObject(String.class));
    
    listener.authenticationAttempted(auditApplicationEvent);
    
    verify(auditApplicationEvent, times(1)).getAuditEvent();
    verify(auditEvent, times(2)).getPrincipal();
    verify(auditEvent, times(1)).getType();
    verify(auditEvent, times(1)).getData();
    verifyNoMoreInteractions(auditApplicationEvent);
    verifyNoMoreInteractions(auditEvent);
    verifyNoMoreInteractions(webAuthenticationDetails);
    
    reset(auditApplicationEvent);
    reset(auditEvent);
    reset(webAuthenticationDetails);
  }
}
