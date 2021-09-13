package edu.duke.rs.baseProject.repository;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.data.repository.Repository;

import edu.duke.rs.baseProject.model.BaseEntity;

@NoRepositoryBean
public interface ReadOnlyRepository<T extends BaseEntity, ID extends Serializable> extends Repository<T, ID> {
  List<T> findAll();
  List<T> findAll(Sort sort);
  Page<T> findAll(Pageable pageable);
  Optional<T> findById(ID id);
  long count();
  Optional<T> findByAlternateId(UUID alternateId);
  Optional<T> findByAlternateId(UUID alternateId, String entityGraphName);
}