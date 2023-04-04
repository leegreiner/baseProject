package edu.duke.rs.baseProject.config;

import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.TransactionDefinition;

import edu.duke.rs.baseProject.audit.AuditContextHolder;
import jakarta.persistence.EntityManagerFactory;

@SuppressWarnings("serial")
public class AuditingJpaTransactionManager extends JpaTransactionManager {
  private final AuditContextHolder auditContextHolder = new AuditContextHolder();
  
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
