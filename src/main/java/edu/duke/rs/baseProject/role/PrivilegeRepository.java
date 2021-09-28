package edu.duke.rs.baseProject.role;

import java.util.Optional;

import edu.duke.rs.baseProject.repository.ReadOnlyRepository;

public interface PrivilegeRepository extends ReadOnlyRepository<Privilege, Long> {
  Optional<Privilege> findByName(PrivilegeName name);
}
