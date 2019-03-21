package edu.duke.rs.baseProject.user;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	List<UserListItem> findByUserNameStartingWithIgnoreCase(String userName, Pageable pageable);
}
