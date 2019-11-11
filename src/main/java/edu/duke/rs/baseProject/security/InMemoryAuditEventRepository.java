package edu.duke.rs.baseProject.security;

import java.time.Instant;
import java.util.LinkedList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.AuditEventRepository;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
public class InMemoryAuditEventRepository implements AuditEventRepository {
  private static final int DEFAULT_CAPACITY = 1000;
  private final Object monitor = new Object();
  // Circular buffer of the event with tail pointing to the last element.
  private AuditEvent[] events;
  private volatile int tail = -1;
  private final String managementUserName;
  
  public InMemoryAuditEventRepository(@Value("${app.management.userName}") final String managementUserName) {
    this.events = new AuditEvent[DEFAULT_CAPACITY];
    this.managementUserName = managementUserName;
  }

  /**
   * Set the capacity of this event repository.
   * @param capacity the capacity
   */
  public void setCapacity(int capacity) {
    synchronized (this.monitor) {
      this.events = new AuditEvent[capacity];
    }
  }

  @Override
  public void add(final AuditEvent event) {
    Assert.notNull(event, "AuditEvent must not be null");
    
    if (managementUserName.equals(event.getPrincipal())) {
      // ignore management user login requests as admin server logs in frequently
      return;
    }
    
    synchronized (this.monitor) {
      this.tail = (this.tail + 1) % this.events.length;
      this.events[this.tail] = event;
    }
  }

  @Override
  public List<AuditEvent> find(final String principal, final Instant after, final String type) {
    final LinkedList<AuditEvent> events = new LinkedList<>();
    synchronized (this.monitor) {
      for (int i = 0; i < this.events.length; i++) {
        final AuditEvent event = resolveTailEvent(i);
        if (event != null && isMatch(principal, after, type, event)) {
          events.addFirst(event);
        }
      }
    }
    return events;
  }

  private boolean isMatch(final String principal, final Instant after, final String type, final AuditEvent event) {
    boolean match = (principal == null || event.getPrincipal().equals(principal));
    match = match && (after == null || event.getTimestamp().isAfter(after));
    match = match && (type == null || event.getType().equals(type));
    return match;
  }

  private AuditEvent resolveTailEvent(int offset) {
    int index = ((this.tail + this.events.length - offset) % this.events.length);
    return this.events[index];
  }
}
