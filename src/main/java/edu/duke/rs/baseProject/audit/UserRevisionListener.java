package edu.duke.rs.baseProject.audit;

import java.util.Optional;

import org.hibernate.envers.RevisionListener;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

public class UserRevisionListener implements RevisionListener {
  private transient final SecurityUtils securityUtils;
  public static final String SYSTEM_USER = "System";
  
  public UserRevisionListener(final SecurityUtils securityUtils) {
    this.securityUtils = securityUtils;
  }
  
  public UserRevisionListener() {
    this(new SecurityUtils());
  }
  
  @Override
  public void newRevision(Object revisionEntity) {
    final Optional<AppPrincipal> principal = securityUtils.getPrincipal();
    final AuditRevisionEntity are = (AuditRevisionEntity) revisionEntity;
    
    if (principal.isPresent()) {    
      are.setUserId(principal.get().getUserId());
      are.setInitiator(principal.get().getDisplayName());
    } else {
      are.setInitiator(SYSTEM_USER);
    }
  }

}
