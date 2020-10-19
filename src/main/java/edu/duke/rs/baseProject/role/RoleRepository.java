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
  @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
    @QueryHint(name = org.hibernate.annotations.QueryHints.CACHE_REGION, value = "noExpireQueryResultsRegion")
  })
  List<Role> findAll(Sort sort);
  @QueryHints({@QueryHint(name = org.hibernate.annotations.QueryHints.CACHEABLE, value = "true"),
    @QueryHint(name = org.hibernate.annotations.QueryHints.CACHE_REGION, value = "noExpireQueryResultsRegion")
  })
	Optional<Role> findByName(RoleName name);
}
