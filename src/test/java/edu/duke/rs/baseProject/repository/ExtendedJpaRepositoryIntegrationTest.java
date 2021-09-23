package edu.duke.rs.baseProject.repository;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import java.util.HashSet;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.jeasy.random.randomizers.EmailRandomizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.duke.rs.baseProject.AbstractRepositoryTest;
import edu.duke.rs.baseProject.UserBuilder;
import edu.duke.rs.baseProject.UsrBuilder;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserConstants;
import edu.duke.rs.baseProject.user.UserRepository;

@ExtendWith(SpringExtension.class)
public class ExtendedJpaRepositoryIntegrationTest extends AbstractRepositoryTest {
  private static final EmailRandomizer EMAIL_RANDOMIZER = new EmailRandomizer(new Random().nextLong());
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  private final UsrBuilder userBuilder = new UserBuilder();

  @Test
  public void whenFindByAlternateIdNotFound_thenReturnsEmptyOptional() {
    Optional<User> userOptional = this.userRepository.findByAlternateId(UUID.randomUUID());
    
    assertThat(userOptional.isPresent(), equalTo(false));
    
    userOptional = this.userRepository.findByAlternateId(UUID.randomUUID(), UserConstants.USER_AND_ROLES_ENTITY_GRAPH);
    
    assertThat(userOptional.isPresent(), equalTo(false));
    
    userOptional = this.userRepository.findByAlternateId(UUID.randomUUID(), "");
    
    assertThat(userOptional.isPresent(), equalTo(false));
    
    final Optional<Role> roleOptional = this.roleRepository.findByAlternateId(UUID.randomUUID());
    
    assertThat(roleOptional.isPresent(), equalTo(false));
  }
  
  @Test
  public void whenFindByAlternateIdIsFound_thenReturnsEntity() {
    final Role role = testEntityManager.persist(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    User user = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user.setRoles(roles);
    user = testEntityManager.persistAndFlush(user);
    
    Optional<User> userOptional = this.userRepository.findByAlternateId(user.getAlternateId());
    
    assertThat(userOptional.isPresent(), equalTo(true));
    assertThat(userOptional.get().getAlternateId(), equalTo(user.getAlternateId()));
    assertThat(userOptional.get().getRoles().size(), equalTo(user.getRoles().size()));
    
    userOptional = this.userRepository.findByAlternateId(user.getAlternateId(), UserConstants.USER_AND_ROLES_ENTITY_GRAPH);
    
    assertThat(userOptional.isPresent(), equalTo(true));
    assertThat(userOptional.get().getAlternateId(), equalTo(user.getAlternateId()));
    assertThat(userOptional.get().getRoles().size(), equalTo(user.getRoles().size()));
    
    Optional<Role> roleOptional = this.roleRepository.findByAlternateId(role.getAlternateId());
    
    assertThat(roleOptional.isPresent(), equalTo(true));
    assertThat(roleOptional.get().getAlternateId(), equalTo(role.getAlternateId()));
  }
}
