package edu.duke.rs.baseProject.role;

import java.util.List;
import java.util.Optional;

import javax.persistence.QueryHint;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import edu.duke.rs.baseProject.repository.ExtendedJpaRepository;

@Repository
public interface RoleRepository extends ExtendedJpaRepository<Role, Long> {
  @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
    @QueryHint(name = org.hibernate.annotations.QueryHints.CACHE_REGION, value = "noExpireQueryResultsRegion")
  })
  List<Role> findAll(Sort sort);
  @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
    @QueryHint(name = org.hibernate.annotations.QueryHints.CACHE_REGION, value = "noExpireQueryResultsRegion")
  })
	Optional<Role> findByName(RoleName name);

  @EntityGraph(RoleConstants.ROLE_AND_PRIVILEGES_ENTITY_GRAPH)
  Optional<Role> getByName(RoleName name);
}
