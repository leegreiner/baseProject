package edu.duke.rs.baseProject.role;

import java.util.List;
import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  @Cacheable(value = "role", keyGenerator = "methodNameAndParametersKeyGenerator")
  List<Role> findAll(Sort sort);
  
  @Cacheable(value = "role", keyGenerator = "methodNameAndParametersKeyGenerator")
	Optional<Role> findByName(RoleName name);
}
