package edu.duke.rs.baseProject.audit;

import java.util.Optional;

import org.hibernate.envers.RevisionListener;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

public class UserRevisionListener implements RevisionListener {
 @Override
  public void newRevision(Object revisionEntity) {
    final Optional<AppPrincipal> principal = SecurityUtils.getPrincipal();
    
    if (principal.isPresent()) {
      AuditRevisionEntity are = (AuditRevisionEntity) revisionEntity;
      are.setUserId(principal.get().getUserId());
    }
  }

}
