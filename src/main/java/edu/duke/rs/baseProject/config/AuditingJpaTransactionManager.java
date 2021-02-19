package edu.duke.rs.baseProject.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import edu.duke.rs.baseProject.audit.AuditContextHolder;

public class AuditingJpaTransactionManager extends JpaTransactionManager {
  private static final long serialVersionUID = 1L;
  final AuditContextHolder auditContextHolder = new AuditContextHolder();
  
  public AuditingJpaTransactionManager() {
    super();
  }

  public AuditingJpaTransactionManager(EntityManagerFactory emf) {
    super(emf);
  }
  
  @Override
  protected void doBegin(Object transaction, TransactionDefinition definition) {
    this.auditContextHolder.clearContext();
    super.doBegin(transaction, definition);
  }

  @Override
  protected void doCleanupAfterCompletion(Object transaction) {
    this.auditContextHolder.clearContext();
    super.doCleanupAfterCompletion(transaction);
  }
}
