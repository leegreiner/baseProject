package edu.duke.rs.baseProject.user;

import java.util.List;
import java.util.UUID;

import org.springframework.validation.annotation.Validated;

import edu.duke.rs.baseProject.role.Role;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

@Validated
public interface UserService {
  UserProfile getUserProfile();
  void updateUserProfile(@Valid UserProfile userProfile);
  User getUser(@NotNull UUID userId);
  List<Role> getRoles();
  User save(@Valid UserDto userDto);
  void disableUnusedAccounts();
}
