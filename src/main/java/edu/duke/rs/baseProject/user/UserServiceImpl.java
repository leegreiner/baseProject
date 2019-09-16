package edu.duke.rs.baseProject.user;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.event.CreatedEvent;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.security.password.PasswordGenerator;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UserServiceImpl implements UserService {
	private transient final UserRepository userRepository;
	private transient final RoleRepository roleRepository;
	private transient final PasswordGenerator passwordGenerator;
	private transient final PasswordResetService passwordResetService;
	private transient final ApplicationEventPublisher eventPublisher;
	
	public UserServiceImpl(final UserRepository userRepository,
	    final RoleRepository roleRepository, final PasswordGenerator passwordGenerator,
	    final PasswordResetService passwordResetService,
	    final ApplicationEventPublisher eventPublisher) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordGenerator = passwordGenerator;
		this.passwordResetService = passwordResetService;
		this.eventPublisher = eventPublisher;
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
  @Transactional(propagation = Propagation.REQUIRED)
  public User save(final UserDto userDto) {
    log.debug("In save(): " + userDto.toString());
    return userDto.getId() == null ? createUser(userDto) : saveUser(userDto);
  }
  
  private User saveUser(final UserDto userDto) {
    final Optional<User> userWithEmail = this.userRepository.findByEmailIgnoreCase(userDto.getEmail());
    
    if (userWithEmail.isPresent() && ! userWithEmail.get().getId().equals(userDto.getId())) {
      throw new ConstraintViolationException("error.duplicateEmail", new Object[] {userDto.getEmail()});
    }
    
    User user = this.getUser(userDto.getId());
    
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
    
    User user = new User();
    
    user.setAccountEnabled(userDto.isAccountEnabled());
    user.setEmail(userDto.getEmail().trim());
    user.setFirstName(userDto.getFirstName().trim());
    user.setMiddleInitial(userDto.getMiddleInitial());
    user.setLastName(userDto.getLastName().trim());
    user.setPassword(this.passwordGenerator.generate());
    user.setTimeZone(userDto.getTimeZone());
    user.setUserName(userDto.getUserName().trim());
    passwordResetService.initiatePasswordReset(user);
    
    populateRoles(userDto, user);
    
    user = userRepository.save(user);
    
    eventPublisher.publishEvent(new CreatedEvent<User>(user));
    
    return user;
  }
  
  private void populateRoles(final UserDto userDto, final User user) {
    if (user.getRoles() == null) {
      user.setRoles(new HashSet<Role>());
    }
    
    final Set<RoleName> inComingRoleNames = userDto.getRoles().stream().map(s -> RoleName.valueOf(s)).collect(Collectors.toSet());
    final Set<RoleName> currentRoleNames = user.getRoles().stream().map(r -> r.getName()).collect(Collectors.toSet());
    final Set<RoleName> rolesToRemove = new HashSet<RoleName>(currentRoleNames);
    final Set<RoleName> rolesToAdd = new HashSet<RoleName>(inComingRoleNames);
    
    rolesToRemove.removeAll(inComingRoleNames);
    rolesToAdd.removeAll(currentRoleNames);
    
    for (final Iterator<Role> iter = user.getRoles().iterator(); iter.hasNext();) {
      final Role role = iter.next();
      
      if (rolesToRemove.contains(role.getName())) {
        iter.remove();
      }
    }
    
    rolesToAdd
      .stream()
      .forEach(roleName -> {
        final Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new NotFoundException("error.roleNotFound", new Object[] {roleName}));
        user.getRoles().add(role);
      }
    );
  }
}
