package edu.duke.rs.baseProject.user.history;

import org.hibernate.envers.RevisionType;

import edu.duke.rs.baseProject.audit.AuditRevisionEntity;
import edu.duke.rs.baseProject.audit.EntityHistory;
import edu.duke.rs.baseProject.user.User;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(callSuper = true)
public class UserHistory extends EntityHistory {
  private final User user;
  
  public UserHistory(final User user, final AuditRevisionEntity auditRevisionEntity, final RevisionType revisionType) {
    super(auditRevisionEntity, revisionType);
    this.user = user;
  }
}
