package edu.duke.rs.baseProject.service;

import edu.duke.rs.baseProject.audit.AuditContextHolder;
import edu.duke.rs.baseProject.audit.AuditContextHolder.AuditContext;

public class AuditableService {
  private final AuditContextHolder auditContextHolder = new AuditContextHolder();
  
  protected AuditContext getAuditContext() {
    return this.auditContextHolder.getContext();
  }
}
