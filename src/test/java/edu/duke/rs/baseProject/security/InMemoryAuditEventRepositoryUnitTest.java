package edu.duke.rs.baseProject.security;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.springframework.boot.actuate.audit.AuditEvent;

public class InMemoryAuditEventRepositoryUnitTest {
  private static final String MGMT_USER = "mgmt_user";
  
  @Test
  public void whenIgnoredPrincipal_thenEventNotAddedToRepository() {
    final InMemoryAuditEventRepository listener = new InMemoryAuditEventRepository(MGMT_USER);
    final AuditEvent event = new AuditEvent(Instant.now(), MGMT_USER, "type1", new HashMap<String, Object>());
    
    listener.add(event);
    
    assertThat(listener.find(event.getPrincipal(), event.getTimestamp(), event.getType()), empty());
  }
  
  @Test
  public void whenNotIgnoredPrincipal_thenEventAddedToRepository() {
    final InMemoryAuditEventRepository listener = new InMemoryAuditEventRepository(MGMT_USER);
    final Instant now = Instant.now();
    final AuditEvent event = new AuditEvent(now, "principal1", "type1", new HashMap<String, Object>());
    
    listener.add(event);
    
    final List<AuditEvent> events = listener.find(event.getPrincipal(), event.getTimestamp().minusMillis(1000), event.getType());
    
    assertThat(events, contains(event));
  }
  
  @Test
  public void whenEventFound_thenFindReturnsEvent() {
    final InMemoryAuditEventRepository listener = new InMemoryAuditEventRepository(MGMT_USER);
    final Instant now = Instant.now();
    final AuditEvent event = new AuditEvent(now, "principal1", "type1", new HashMap<String, Object>());
    
    listener.add(event);
    
    assertThat(listener.find(event.getPrincipal(), event.getTimestamp().minusMillis(1000), event.getType()), contains(event));
    assertThat(listener.find(event.getPrincipal(), event.getTimestamp().minusMillis(1000), null), contains(event));
    assertThat(listener.find(event.getPrincipal(), null, event.getType()), contains(event));
    assertThat(listener.find(null, event.getTimestamp().minusMillis(1000), event.getType()), contains(event));
    assertThat(listener.find(event.getPrincipal(), null, null), contains(event));
    assertThat(listener.find(null, null, event.getType()), contains(event));
    assertThat(listener.find(null, event.getTimestamp().minusMillis(1000), null), contains(event));
    assertThat(listener.find(null, null, null), contains(event));
  }
  
  @Test
  public void whenEventNotFound_thenFindDoesntReturnEvent() {
    final InMemoryAuditEventRepository listener = new InMemoryAuditEventRepository(MGMT_USER);
    final Instant now = Instant.now();
    final AuditEvent event = new AuditEvent(now, "principal1", "type1", new HashMap<String, Object>());
    
    listener.add(event);
    
    assertThat(listener.find(event.getPrincipal() + "a", event.getTimestamp().minusMillis(1000), event.getType()), empty());
    assertThat(listener.find(event.getPrincipal(), event.getTimestamp(), null), empty());
    assertThat(listener.find(event.getPrincipal(), null, event.getType() + "A"), empty());
  }
  
  @Test
  public void whenChangingCapacity_thenEventsAreCleared() {
    final InMemoryAuditEventRepository listener = new InMemoryAuditEventRepository(MGMT_USER);
    final Instant now = Instant.now();
    final AuditEvent event = new AuditEvent(now, "principal1", "type1", new HashMap<String, Object>());
    
    listener.add(event);
    listener.setCapacity(10);
    
    assertThat(listener.find(null, null, null), empty());
  }
}
