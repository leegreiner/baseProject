package edu.duke.rs.baseProject.audit;

import org.hibernate.envers.RevisionType;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class AuditQueryResult<T> {
  private final T entity;
  private final AuditRevisionEntity revision;
  private final RevisionType type;
  
  public AuditQueryResult(final T entity, final AuditRevisionEntity revision, final RevisionType type) {
    this.entity = entity;
    this.revision = revision;
    this.type = type;
  }
}
