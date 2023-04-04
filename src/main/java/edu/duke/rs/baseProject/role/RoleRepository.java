package edu.duke.rs.baseProject.role;

import java.util.List;
import java.util.Optional;

import org.hibernate.jpa.AvailableHints;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.QueryHints;

import edu.duke.rs.baseProject.repository.ReadOnlyRepository;
import jakarta.persistence.QueryHint;

public interface RoleRepository extends ReadOnlyRepository<Role, Long> {
  @QueryHints({@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"),
    @QueryHint(name = AvailableHints.HINT_CACHE_REGION, value = "noExpireQueryResultsRegion")
  })
  List<Role> findAll(Sort sort);
  @QueryHints({@QueryHint(name = AvailableHints.HINT_CACHEABLE, value = "true"),
    @QueryHint(name = AvailableHints.HINT_CACHE_REGION, value = "noExpireQueryResultsRegion")
  })
	Optional<Role> findByName(RoleName name);

  @EntityGraph(RoleConstants.ROLE_AND_PRIVILEGES_ENTITY_GRAPH)
  Optional<Role> getByName(RoleName name);
}
