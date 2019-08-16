package edu.duke.rs.baseProject.user;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.security.password.PasswordGenerator;

@Service
public class UserServiceImpl implements UserService {
	private transient final UserRepository userRepository;
	private transient final RoleRepository roleRepository;
	private transient final PasswordGenerator passwordGenerator;
	
	public UserServiceImpl(final UserRepository userRepository,
	    final RoleRepository roleRepository, final PasswordGenerator passwordGenerator) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordGenerator = passwordGenerator;
	}
	
	@Override
  public UserProfile getUserProfile() {
    final User user = userRepository.findById(getCurrentUser().getUserId())
        .orElseThrow(() -> new NotFoundException("error.principalNotFound", (Object[])null));

    return new UserProfile(user.getTimeZone());
  }

  @Override
  @Transactional
  public void updateUserProfile(final UserProfile userProfile) {
    final User user = userRepository.findById(getCurrentUser().getUserId())
        .orElseThrow(() -> new NotFoundException("error.principalNotFound", (Object[])null));
    
    user.setTimeZone(userProfile.getTimeZone());
  }
  

  @Override
  public User getUser(final Long userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new NotFoundException("error.userNotFound", new Object[] {userId}));
  }
  
  private AppPrincipal getCurrentUser() {
    return SecurityUtils.getPrincipal().orElseThrow(() ->  new IllegalArgumentException("error.principalNotFound"));
  }

  @Override
  public List<Role> getRoles() {
    return roleRepository.findAll(new Sort(Sort.Direction.ASC, "name"));
  }

  @Override
  @Transactional
  public User save(final UserDto userDto) {
    return userDto.getId() == null ? createUser(userDto) : saveUser(userDto);
  }
  
  private User saveUser(final UserDto userDto) {
    final Optional<User> userWithEmail = this.userRepository.findByEmailIgnoreCase(userDto.getEmail());
    
    if (userWithEmail.isPresent() && ! userWithEmail.get().getId().equals(userDto.getId())) {
      throw new ConstraintViolationException("error.duplicateEmail", new Object[] {userDto.getEmail()});
    }
    
    final User user = this.getUser(userDto.getId());
    
    user.setAccountEnabled(userDto.isAccountEnabled());
    user.setEmail(userDto.getEmail().trim());
    user.setFirstName(userDto.getFirstName().trim());
    user.setLastName(userDto.getLastName().trim());
    user.setMiddleInitial(userDto.getMiddleInitial());
    user.setTimeZone(userDto.getTimeZone());
    
    populateRoles(userDto, user);
    
    return userRepository.save(user);
  }
  
  private User createUser(final UserDto userDto) {
    if (this.userRepository.findByUserNameIgnoreCase(userDto.getUserName()).isPresent()) {
      throw new ConstraintViolationException("error.duplicateUserName", new Object[] {userDto.getUserName()});
    }
    
    if (this.userRepository.findByEmailIgnoreCase(userDto.getEmail()).isPresent()) {
      throw new ConstraintViolationException("error.duplicateEmail", new Object[] {userDto.getEmail()});
    }
    
    final User user = new User();
    
    user.setAccountEnabled(userDto.isAccountEnabled());
    user.setEmail(userDto.getEmail().trim());
    user.setFirstName(userDto.getFirstName().trim());
    user.setMiddleInitial(userDto.getMiddleInitial());
    user.setLastName(userDto.getLastName().trim());
    user.setPassword(this.passwordGenerator.generate());
    user.setTimeZone(userDto.getTimeZone());
    user.setUserName(userDto.getUserName().trim());
    
    populateRoles(userDto, user);
    
    return userRepository.save(user);
  }
  
  private void populateRoles(final UserDto userDto, final User user) {
    if (user.getRoles() == null) {
      user.setRoles(new HashSet<Role>());
    } else {
      user.getRoles().clear();
    }
    
    userDto.getRoles()
      .stream()
      .forEach(r -> {
        final Role role = roleRepository.findByName(RoleName.valueOf(r))
            .orElseThrow(() -> new NotFoundException("error.roleNotFound", new Object[] {r}));
        user.getRoles().add(role);
      }
    );
  }
}
