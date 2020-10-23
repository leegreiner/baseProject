package edu.duke.rs.baseProject.audit;

import org.hibernate.envers.RevisionListener;

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
    final AuditRevisionEntity are = (AuditRevisionEntity) revisionEntity;
    
    securityUtils.getPrincipal().ifPresentOrElse((appPrincipal) -> {
      are.setUserId(appPrincipal.getUserId());
      are.setInitiator(appPrincipal.getDisplayName());
    }, () -> are.setInitiator(SYSTEM_USER));
  }
}
