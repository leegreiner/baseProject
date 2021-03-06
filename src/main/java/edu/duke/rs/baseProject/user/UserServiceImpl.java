package edu.duke.rs.baseProject.user;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.duke.rs.baseProject.event.CreatedEvent;
import edu.duke.rs.baseProject.event.UpdatedEvent;
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;
import edu.duke.rs.baseProject.security.password.PasswordGenerator;
import edu.duke.rs.baseProject.service.AuditableService;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@RequiredArgsConstructor
@Log4j2
@Service
public class UserServiceImpl extends AuditableService implements UserService {
  private transient final UserRepository userRepository;
  private transient final RoleRepository roleRepository;
  private transient final PasswordGenerator passwordGenerator;
  private transient final PasswordResetService passwordResetService;
  private transient final ApplicationEventPublisher eventPublisher;
  private transient final SecurityUtils securityUtils;
  @Setter(AccessLevel.PACKAGE) // for testing
  @Value("${app.security.disableUnusedAccountsGreaterThanMonths:12}")
  private Long disableUnusedAccountsGreaterThanMonths;
  
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
  public User getUser(final UUID  userId) {
    return userRepository.findByAlternateId(userId, UserConstants.USER_AND_ROLES_ENTITY_GRAPH)
        .orElseThrow(() -> new NotFoundException("error.userNotFound", new Object[] {userId}));
  }
  
  private AppPrincipal getCurrentUser() {
    return securityUtils.getPrincipal().orElseThrow(() ->  new IllegalArgumentException("error.principalNotFound"));
  }

  @Override
  public List<Role> getRoles() {
    return roleRepository.findAll(Sort.by(Sort.Direction.ASC, "name"));
  }

  @Override
  @Transactional
  public User save(final UserDto userDto) {
    log.debug("In save(): {}", () -> userDto.toString());
    return userDto.getId() == null ? createUser(userDto) : saveUser(userDto);
  }
  
  @Override
  @Transactional
  public void disableUnusedAccounts() {
    this.userRepository.disableUnusedAccounts(LocalDateTime.now().minusMonths(disableUnusedAccountsGreaterThanMonths));
  }
  
  private User saveUser(final UserDto userDto) {
    if (this.getCurrentUser().getAlternateUserId().equals(userDto.getId())) {
      throw new ConstraintViolationException("error.cantUpdateOwnAccount", (Object[]) null);
    }
    
    final Optional<User> userWithEmail = this.userRepository.findByEmailIgnoreCase(userDto.getEmail());
    
    if (userWithEmail.isPresent() && ! userWithEmail.get().getAlternateId().equals(userDto.getId())) {
      throw new ConstraintViolationException("error.duplicateEmail", new Object[] {userDto.getEmail()});
    }
    
    User user = userWithEmail.isPresent() ? userWithEmail.get() : this.getUser(userDto.getId());
    
    user.setAccountEnabled(userDto.isAccountEnabled());
    user.setEmail(userDto.getEmail().trim());
    user.setFirstName(userDto.getFirstName().trim());
    user.setLastName(userDto.getLastName().trim());
    user.setMiddleInitial(userDto.getMiddleInitial());
    user.setTimeZone(userDto.getTimeZone());
    
    this.getAuditContext().setReasonForChange(userDto.getReasonForChange());
    
    populateRoles(userDto, user);
    
    user = userRepository.save(user);
    
    eventPublisher.publishEvent(new UpdatedEvent<User>(user));
    
    return user;
  }
  
  private User createUser(final UserDto userDto) {
    if (this.userRepository.findByUsernameIgnoreCase(userDto.getUsername()).isPresent()) {
      throw new ConstraintViolationException("error.duplicateUserName", new Object[] {userDto.getUsername()});
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
    user.setPassword(this.passwordGenerator.generate()); // set to a password that can't be hashed
    user.setTimeZone(userDto.getTimeZone());
    user.setUsername(userDto.getUsername().trim());
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
    
    final Set<RoleName> inComingRoleNames = new HashSet<RoleName>(userDto.getRoles());
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
