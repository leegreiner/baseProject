package edu.duke.rs.baseProject.user;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Page<UserListItem> findByLastNameStartingWithIgnoreCase(String userName, Pageable pageable);
	Page<UserListItem> findAllBy(Pageable pageable);
	@EntityGraph("user.userAndRoles")
	Optional<User> findByUserNameIgnoreCase(String userName);
	Optional<User> findByEmailIgnoreCase(String email);
	@EntityGraph("user.userAndRoles")
  Optional<User> findById(Long id);
	Optional<User> findByPasswordChangeId(UUID passwordChangeId);
	@Modifying
	@Query("update User user set user.passwordChangeId = null, user.passwordChangeIdCreationTime = null where user.passwordChangeIdCreationTime < :time")
	void expirePasswordChangeIds(@Param("time") LocalDateTime time);
}
