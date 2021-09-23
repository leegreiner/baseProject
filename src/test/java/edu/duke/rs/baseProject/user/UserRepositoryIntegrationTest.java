package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import org.jeasy.random.randomizers.EmailRandomizer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.duke.rs.baseProject.AbstractRepositoryTest;
import edu.duke.rs.baseProject.UserBuilder;
import edu.duke.rs.baseProject.UsrBuilder;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;

@ExtendWith(SpringExtension.class)
public class UserRepositoryIntegrationTest extends AbstractRepositoryTest {
  private static final EmailRandomizer EMAIL_RANDOMIZER = new EmailRandomizer(new Random().nextLong());
  @Autowired
  private UserRepository userRepository;
  private final UsrBuilder userBuilder = new UserBuilder();
  
  @Test
  public void whenFindByUserNameStartingWithIgnoreCaseNoUsers_thenReturnNoUsers() {
    final Page<UserListItem> page = userRepository.findByLastNameStartingWithIgnoreCase(
        "j", PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "username")));
    
    final List<UserListItem> users = page.getContent();
    
    assertThat(users, notNullValue());
    assertThat(users.size(), equalTo(0));
  }
  
  @Test
  public void whenFindByUserNameStartingWithIgnoreCaseUsers_thenReturnUsers() {
    final Role role = testEntityManager.persist(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    User user1 = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user1.setLastName("Smith");
    user1.setRoles(roles);
    user1 = testEntityManager.persist(user1);
    User user2 = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user2.setLastName("Johnson");
    user2.setRoles(roles);
    user2 = testEntityManager.persist(user2);
    User user3 = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user3.setLastName("Jackson");
    user3.setRoles(roles);
    user3 = testEntityManager.persistAndFlush(user3);
    testEntityManager.clear();
    
    final Page<UserListItem> page = userRepository.findByLastNameStartingWithIgnoreCase(
        "j", PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "lastName")));
    
    final List<User> expectedResult = new ArrayList<>(List.of(user3, user2));
    final List<UserListItem> users = page.getContent();
    
    assertThat(users, notNullValue());
    assertThat(users.size(), equalTo(expectedResult.size()));
    
    for (int i = 0; i < users.size(); i++) {
      assertThat(users.get(i).getId(), equalTo(expectedResult.get(i).getAlternateId()));
      assertThat(users.get(i).getFirstName(), equalTo(expectedResult.get(i).getFirstName()));
      assertThat(users.get(i).getLastName(), equalTo(expectedResult.get(i).getLastName()));
    }
  }
  
  @Test
  public void whenExpirePasswordChangeIds_thenUserPasswordResetFieldsAreNulled() {
    final LocalDateTime now = LocalDateTime.now();
    final Role role = testEntityManager.persist(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    User user1 = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user1.setRoles(roles);
    user1.setPasswordChangeId(UUID.randomUUID());
    user1.setPasswordChangeIdCreationTime(now.minusDays(3));
    user1 = testEntityManager.persist(user1);
    User user2 = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user2.setRoles(roles);
    user2.setPasswordChangeId(UUID.randomUUID());
    user2.setPasswordChangeIdCreationTime(now.minusDays(1));
    user2 = testEntityManager.persistAndFlush(user2);
    
    this.userRepository.expirePasswordChangeIds(now.minusDays(2));
    testEntityManager.clear();
    
    user1 = this.userRepository.getById(user1.getId());
    assertThat(user1.getPasswordChangeId(), nullValue());
    assertThat(user1.getPasswordChangeIdCreationTime(),nullValue());
    user2 = this.userRepository.getById(user2.getId());
    assertThat(user2.getPasswordChangeId(), notNullValue());
    assertThat(user2.getPasswordChangeIdCreationTime(), notNullValue());
  }
  
  @Test
  public void whenDisableUnusedAccounts_thenUnusedAccountsDisabled() {
    final Role role = testEntityManager.persist(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    User user1 = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user1.setRoles(roles);
    user1.setLastLoggedIn(LocalDateTime.now().minusYears(1).minusMinutes(1));
    user1.setAccountEnabled(true);
    testEntityManager.persist(user1);
    User user2 = userBuilder.build(easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), easyRandom.nextObject(String.class),
        easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), null);
    user2.setRoles(roles);
    user2.setLastLoggedIn(LocalDateTime.now().minusYears(1).plusMinutes(1));
    user2.setAccountEnabled(true);
    user2 = testEntityManager.persistAndFlush(user2);
    
    this.userRepository.disableUnusedAccounts(LocalDateTime.now().minusYears(1));
    testEntityManager.clear();
    
    user1 = this.userRepository.findById(user1.getId()).get();
    user2 = this.userRepository.findById(user2.getId()).get();
    
    assertThat(user1.isAccountEnabled(), equalTo(false));
    assertThat(user2.isAccountEnabled(), equalTo(true));
  }
}
