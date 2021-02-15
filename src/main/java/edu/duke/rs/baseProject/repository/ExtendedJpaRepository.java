package edu.duke.rs.baseProject.repository;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.NoRepositoryBean;

import edu.duke.rs.baseProject.model.BaseEntity;

@NoRepositoryBean
public interface ExtendedJpaRepository<T extends BaseEntity, ID extends Serializable>
  extends JpaRepository<T, ID>,  JpaSpecificationExecutor<T> {
  Optional<T> findByAlternateId(UUID alternateId);
  Optional<T> findByAlternateId(UUID alternateId, String entityGraphName);
}
