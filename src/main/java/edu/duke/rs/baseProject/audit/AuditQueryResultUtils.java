package edu.duke.rs.baseProject.audit;

import org.hibernate.envers.RevisionType;

public class AuditQueryResultUtils {
  private AuditQueryResultUtils() {}

  public static <T> AuditQueryResult<T> getAuditQueryResult(Object[] item, Class<T> type) {
    // Early exit, if no item given, or not enough data
    if(item == null || item.length < 3) {
      return null;
    }

    // Cast item[0] to the Entity:
    T entity = null;
    if(type.isInstance(item[0])) {
      entity = type.cast(item[0]);
    }

    // Then get the Revision Entity:
    AuditRevisionEntity revision = null;
    if(item[1] instanceof AuditRevisionEntity) {
      revision = (AuditRevisionEntity) item[1];
    }

    // Then get the Revision Type:
    RevisionType revisionType = null;
    if(item[2] instanceof RevisionType) {
      revisionType = (RevisionType) item[2];
    }

    // Build the Query Result:
    return new AuditQueryResult<T>(entity, revision, revisionType);
  }
}
