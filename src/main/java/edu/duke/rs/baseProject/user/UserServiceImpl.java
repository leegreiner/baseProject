package edu.duke.rs.baseProject.user;

import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

@Service
public class UserServiceImpl implements UserService {
	private transient final UserRepository userRepository;
	
	public UserServiceImpl(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	@Override
  public UserProfile getUserProfile() {
    final User user = userRepository.findById(getCurrentUser().getUserId())
        .orElseThrow(() -> new NotFoundException("error.userNotFound"));
    
    final UserProfile userProfile = new UserProfile(user.getTimeZone());
    return userProfile;
    
  }

  @Override
  @Transactional
  public void updateUserProfile(UserProfile userProfile) {
    final User user = userRepository.findById(getCurrentUser().getUserId())
        .orElseThrow(() -> new NotFoundException("error.userNotFound"));
    
    user.setTimeZone(userProfile.getTimeZone());
    
  }
  

  @Override
  public User getUser(@NotNull Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("error.userNotFound"));
  }
  
  private AppPrincipal getCurrentUser() {
    final AppPrincipal appPrincipal = SecurityUtils.getPrincipal();
    if(appPrincipal == null) {
      throw new IllegalArgumentException("error.principalNotFound");
    }
    return appPrincipal;
    
  }
}
