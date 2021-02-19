package edu.duke.rs.baseProject.audit;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.hibernate.envers.RevisionType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class EntityHistory {
  private final Number revision;
  private final RevisionType revisionType;
  private final LocalDateTime eventTime;
  private final String initiator;
  private final String reasonForChange;
  
  public EntityHistory(final AuditRevisionEntity auditRevisionEntity, final RevisionType revisionType) {
    this.revision = auditRevisionEntity.getId();
    this.revisionType = revisionType;
    this.eventTime = Instant.ofEpochMilli(auditRevisionEntity.getTimestamp()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    this.initiator = auditRevisionEntity.getInitiator();
    this.reasonForChange = auditRevisionEntity.getReasonForChange();
  }
}
