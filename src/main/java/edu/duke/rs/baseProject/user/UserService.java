package edu.duke.rs.baseProject.user;

import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {
	Page<UserListItem> search(String term, @NotNull Pageable pageable);
}
