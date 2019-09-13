package edu.duke.rs.baseProject.role;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  @Override
  @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
  List<Role> findAll(Sort sort);
  
  @QueryHints(value = {@QueryHint(name = "org.hibernate.cacheable", value = "true")}, forCounting = false)
	Optional<Role> findByName(RoleName name);
}
