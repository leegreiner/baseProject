package edu.duke.rs.baseProject.user;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import edu.duke.rs.baseProject.role.Role;

@Validated
public interface UserService {
  UserProfile getUserProfile();
  void updateUserProfile(@Valid UserProfile userProfile);
  User getUser(@NotNull Long userId);
  List<Role> getRoles();
  User save(@Valid UserDto userDto);
}
