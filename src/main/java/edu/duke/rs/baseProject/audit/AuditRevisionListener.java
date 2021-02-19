package edu.duke.rs.baseProject.audit;

import org.hibernate.envers.RevisionListener;

import edu.duke.rs.baseProject.security.SecurityUtils;

public class AuditRevisionListener implements RevisionListener {
  private final AuditContextHolder auditContextHolder = new AuditContextHolder();
  private transient final SecurityUtils securityUtils;
  public static final String SYSTEM_USER = "System";
  
  public AuditRevisionListener(final SecurityUtils securityUtils) {
    this.securityUtils = securityUtils;
  }
  
  public AuditRevisionListener() {
    this(new SecurityUtils());
  }
  
  @Override
  public void newRevision(Object revisionEntity) {
    final AuditRevisionEntity are = (AuditRevisionEntity) revisionEntity;
    
    securityUtils.getPrincipal().ifPresentOrElse((appPrincipal) -> {
      are.setUserId(appPrincipal.getUserId());
      are.setInitiator(appPrincipal.getDisplayName());
    }, () -> are.setInitiator(SYSTEM_USER));
    
    are.setReasonForChange(this.auditContextHolder.getContext().getReasonForChange());
  }
}
