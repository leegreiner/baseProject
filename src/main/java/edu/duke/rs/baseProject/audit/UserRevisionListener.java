package edu.duke.rs.baseProject.audit;

import org.hibernate.envers.RevisionListener;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

public class UserRevisionListener implements RevisionListener {
 @Override
  public void newRevision(Object revisionEntity) {
    final AppPrincipal principal = SecurityUtils.getPrincipal();
    
    if (principal != null) {
      AuditRevisionEntity are = (AuditRevisionEntity) revisionEntity;
      are.setUserId(principal.getUserId());
    }
  }

}
