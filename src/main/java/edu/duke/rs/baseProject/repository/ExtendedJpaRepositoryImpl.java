package edu.duke.rs.baseProject.repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.jpa.AvailableHints;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;

import edu.duke.rs.baseProject.model.BaseEntity;
import edu.duke.rs.baseProject.model.BaseEntity_;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

public class ExtendedJpaRepositoryImpl<T extends BaseEntity, ID extends Serializable> extends SimpleJpaRepository<T, ID>
    implements ExtendedJpaRepository<T, ID> {
  private final EntityManager entityManager;
  
  public ExtendedJpaRepositoryImpl(final JpaEntityInformation<T, ?> entityInformation, final EntityManager entityManager) {
    super(entityInformation, entityManager);
    this.entityManager = entityManager;
  }
  
  @Override
  public Optional<T> findByAlternateId(final UUID alternateId) {
    return findByAlternateIdInternal(alternateId, null);
  }

  @Override
  public Optional<T> findByAlternateId(final UUID alternateId, final String entityGraphName) {
    return findByAlternateIdInternal(alternateId, entityGraphName);
  }
  
  private Optional<T> findByAlternateIdInternal(final UUID alternateId, final String entityGraphName) {
    final CriteriaBuilder builder = entityManager.getCriteriaBuilder();
    final CriteriaQuery<T> query = builder.createQuery(this.getDomainClass());
    final Root<T> root = query.from(this.getDomainClass());
    
    query.select(root)
      .where(builder.equal(root.get(BaseEntity_.alternateId), alternateId));
    
    final TypedQuery<T> typedQuery = entityManager.createQuery(query);
    
    if (entityGraphName != null && ! entityGraphName.isEmpty()) {
      typedQuery.setHint(AvailableHints.HINT_SPEC_LOAD_GRAPH, entityManager.getEntityGraph(entityGraphName));
    }
    
    try {
      return Optional.ofNullable(typedQuery.getSingleResult());
    } catch (final NoResultException noResultException) {
      return Optional.empty();
    }
  }
}
