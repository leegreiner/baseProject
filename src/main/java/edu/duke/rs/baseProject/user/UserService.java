package edu.duke.rs.baseProject.user;

import javax.validation.Valid;

import org.springframework.validation.annotation.Validated;

@Validated
public interface UserService {
  
  UserProfile getUserProfile();
  
  void updateUserProfile(@Valid UserProfile userProfile);
}
