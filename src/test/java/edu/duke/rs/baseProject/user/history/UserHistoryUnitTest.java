package edu.duke.rs.baseProject.user.history;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.hibernate.envers.RevisionType;
import org.junit.jupiter.api.Test;

import edu.duke.rs.baseProject.audit.AuditRevisionEntity;
import edu.duke.rs.baseProject.user.User;

public class UserHistoryUnitTest {
  @Test
  public void whenConstructorCalled_thenObjectCreatedCorrectly() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(Long.valueOf(1), "John Smith");
    final LocalDateTime now = Instant.ofEpochMilli(1568745972000L).atZone(ZoneId.systemDefault()).toLocalDateTime();
    auditRevisionEntity.setTimestamp(1568745972000L);
    final RevisionType revisionType = RevisionType.ADD;
    final User user = new User();
    user.setId(Long.valueOf(auditRevisionEntity.getId() + 1));
    user.setUsername("abc123Abc");
    
    final UserHistory entityHistory = new UserHistory(user, auditRevisionEntity, revisionType);
    
    assertThat(entityHistory.getEventTime()).isEqualTo(now);
    assertThat(entityHistory.getInitiator()).isEqualTo(auditRevisionEntity.getInitiator());
    assertThat(entityHistory.getRevision()).isEqualTo(auditRevisionEntity.getId());
    assertThat(entityHistory.getRevisionType()).isEqualTo(revisionType);
    assertThat(entityHistory.getUser()).isEqualTo(user);
  }
}
