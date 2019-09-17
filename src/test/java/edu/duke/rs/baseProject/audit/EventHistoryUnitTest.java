package edu.duke.rs.baseProject.audit;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.hibernate.envers.RevisionType;
import org.junit.Test;

public class EventHistoryUnitTest {
  @Test
  public void whenConstructorCalled_thenObjectCreatedCorrectly() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(Long.valueOf(1), "John Smith");
    final LocalDateTime now = Instant.ofEpochMilli(1568745972000L).atZone(ZoneId.systemDefault()).toLocalDateTime();
    auditRevisionEntity.setTimestamp(1568745972000L);
    final RevisionType revisionType = RevisionType.ADD;
    
    final EntityHistory entityHistory = new EntityHistory(auditRevisionEntity, revisionType);
    
    assertThat(entityHistory.getEventTime()).isEqualTo(now);
    assertThat(entityHistory.getInitiator()).isEqualTo(auditRevisionEntity.getInitiator());
    assertThat(entityHistory.getRevision()).isEqualTo(auditRevisionEntity.getId());
    assertThat(entityHistory.getRevisionType()).isEqualTo(revisionType);
  }
}
