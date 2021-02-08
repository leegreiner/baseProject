package edu.duke.rs.baseProject.role;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import edu.duke.rs.baseProject.repository.ExtendedJpaRepository;

@Repository
public interface PrivilegeRepository extends ExtendedJpaRepository<Privilege, Long> {
  Optional<Privilege> findByName(PrivilegeName name);
}
