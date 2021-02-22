package edu.duke.rs.baseProject.audit;

import org.hibernate.envers.RevisionListener;

public class AuditRevisionListener implements RevisionListener {
  public static final String SYSTEM_USER = "System";
  private AuditContextHolder auditContextHolder;
  
  public AuditRevisionListener() {
    this.auditContextHolder = new AuditContextHolder();
  }
  
  public AuditRevisionListener(final AuditContextHolder auditContextHolder) {
    this.auditContextHolder = auditContextHolder;
  }
  
  @Override
  public void newRevision(Object revisionEntity) {
    final AuditRevisionEntity are = (AuditRevisionEntity) revisionEntity;
    
    this.auditContextHolder.getContext().getCurrentUser().ifPresentOrElse((appPrincipal) -> {
      are.setUserId(appPrincipal.getUserId());
      are.setInitiator(appPrincipal.getDisplayName());
    }, () -> are.setInitiator(SYSTEM_USER));
    
    are.setReasonForChange(this.auditContextHolder.getContext().getReasonForChange());
  }
}
