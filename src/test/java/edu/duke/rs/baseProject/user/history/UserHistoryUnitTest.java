package edu.duke.rs.baseProject.user.history;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.hibernate.envers.RevisionType;
import org.junit.jupiter.api.Test;

import edu.duke.rs.baseProject.AbstractBaseTest;
import edu.duke.rs.baseProject.audit.AuditRevisionEntity;
import edu.duke.rs.baseProject.user.User;

public class UserHistoryUnitTest extends AbstractBaseTest {
  @Test
  public void whenConstructorCalled_thenObjectCreatedCorrectly() {
    final AuditRevisionEntity auditRevisionEntity = new AuditRevisionEntity(easyRandom.nextLong(),
        easyRandom.nextObject(String.class), easyRandom.nextObject(String.class));
    final LocalDateTime now = Instant.ofEpochMilli(1568745972000L).atZone(ZoneId.systemDefault()).toLocalDateTime();
    auditRevisionEntity.setTimestamp(1568745972000L);
    final RevisionType revisionType = RevisionType.ADD;
    final User user = new User();
    user.setId(Long.valueOf(auditRevisionEntity.getUserId() + 1));
    user.setUsername(easyRandom.nextObject(String.class));
    
    final UserHistory entityHistory = new UserHistory(user, auditRevisionEntity, revisionType);
    
    assertThat(entityHistory.getEventTime()).isEqualTo(now);
    assertThat(entityHistory.getInitiator()).isEqualTo(auditRevisionEntity.getInitiator());
    assertThat(entityHistory.getRevision()).isEqualTo(auditRevisionEntity.getId());
    assertThat(entityHistory.getRevisionType()).isEqualTo(revisionType);
    assertThat(entityHistory.getReasonForChange()).isEqualTo(auditRevisionEntity.getReasonForChange());
    assertThat(entityHistory.getUser()).isEqualTo(user);
  }
}
