package edu.duke.rs.baseProject.user;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Page<UserListItem> findByUserNameStartingWithIgnoreCase(String userName, Pageable pageable);
	Page<UserListItem> findAllBy(Pageable pageable);
	@EntityGraph("user.userAndRoles")
	Optional<User> findByUserNameIgnoreCase(String userName);
}
