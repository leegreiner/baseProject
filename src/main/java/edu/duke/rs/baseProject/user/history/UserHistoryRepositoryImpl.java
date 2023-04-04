package edu.duke.rs.baseProject.user.history;

import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.envers.AuditReader;
import org.hibernate.envers.AuditReaderFactory;
import org.hibernate.envers.query.AuditEntity;
import org.hibernate.envers.query.AuditQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.audit.AuditQueryResult;
import edu.duke.rs.baseProject.audit.AuditQueryUtils;
import edu.duke.rs.baseProject.user.User;
import jakarta.persistence.EntityManager;

@Repository
public class UserHistoryRepositoryImpl implements UserHistoryRepository {
  private transient final EntityManager entityManager;
  
  public UserHistoryRepositoryImpl(final EntityManager entityManager) {
    this.entityManager = entityManager;
  }

  @Override
  @Transactional(readOnly = true)
  public List<UserHistory> listUserRevisions(final Long userId) {
    final AuditReader auditReader = AuditReaderFactory.get(entityManager);
    final AuditQuery auditQuery = auditReader.createQuery()
        .forRevisionsOfEntity(User.class, false, true)
        .add(AuditEntity.id().eq(userId));
    
    return AuditQueryUtils.getAuditQueryResults(auditQuery, User.class).stream()
        .map(UserHistoryRepositoryImpl::getUserHistory)
        .collect(Collectors.toList());
  }

  private static UserHistory getUserHistory(AuditQueryResult<User> auditQueryResult) {
    return new UserHistory(
        auditQueryResult.getEntity(),
        auditQueryResult.getRevision(),
        auditQueryResult.getType());
  }
}
