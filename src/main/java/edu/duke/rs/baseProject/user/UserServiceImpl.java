package edu.duke.rs.baseProject.user;

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
    final AppPrincipal currentUser = getCurrentUser();
    final User user = userRepository.findById(currentUser.getUserId())
        .orElseThrow(() -> new NotFoundException("User not found: " + currentUser.getUserId()));
    
    final UserProfile userProfile = new UserProfile(user.getTimeZone());
    return userProfile;
    
  }

  @Override
  @Transactional
  public void updateUserProfile(UserProfile userProfile) {
    final AppPrincipal currentUser = getCurrentUser();
    final User user = userRepository.findById(currentUser.getUserId())
        .orElseThrow(() -> new NotFoundException("User not found: " + currentUser.getUserId()));
    
    user.setTimeZone(userProfile.getTimeZone());
    
  }
  
  private AppPrincipal getCurrentUser() {
    final AppPrincipal appPrincipal = SecurityUtils.getPrincipal();
    if(appPrincipal == null) {
      throw new IllegalArgumentException("No current user found.");
    }
    return appPrincipal;
    
  }
}
