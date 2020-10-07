package edu.duke.rs.baseProject.role;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  List<Role> findAll(Sort sort);
	Optional<Role> findByName(RoleName name);
}
