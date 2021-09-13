package edu.duke.rs.baseProject.role;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import edu.duke.rs.baseProject.repository.ReadOnlyRepository;

@Repository
public interface PrivilegeRepository extends ReadOnlyRepository<Privilege, Long> {
  Optional<Privilege> findByName(PrivilegeName name);
}
