package edu.duke.rs.baseProject.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Page<UserListItem> findByLastNameStartingWithIgnoreCase(String lastName, Pageable pageable);
	Page<UserListItem> findAllBy(Pageable pageable);
	
	@EntityGraph("user.userAndRoles")
	Optional<User> findByUsernameIgnoreCase(String username);
	Optional<User> findByEmailIgnoreCase(String email);
	Optional<User> findByPasswordChangeId(UUID passwordChangeId);
	
	@EntityGraph("user.userAndRoles")
	Optional<User> findByAlternateId(UUID alternateId);
	
	@Modifying
	void expirePasswordChangeIds(@Param("time") LocalDateTime time);
	
	@Modifying
  void disableUnusedAccounts(@Param("date") LocalDateTime lastLoggedIn);
}
