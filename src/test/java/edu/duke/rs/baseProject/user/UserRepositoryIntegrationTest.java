package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

import edu.duke.rs.baseProject.AbstractRepositoryTest;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest extends AbstractRepositoryTest {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private UserRepository userRepository;
	
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
		final Role role = entityManager.persist(new Role(RoleName.USER));
		final Set<Role> roles = new HashSet<Role>();
		roles.add(role);
		
		User user1 = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
		user1 = entityManager.persist(user1);
		User user2 = new User("simmyjohnson", "password", "Simmy", "Johnson","simmyJohnson@gmail.com", roles);
		user2 = entityManager.persist(user2);
		User user3 = new User("jimmyjohnson", "password", "Jimmy", "Johnson","jimmyJohnson@gmail.com", roles);
		user3 = entityManager.persistAndFlush(user3);
		entityManager.clear();
		
		final Page<UserListItem> page = userRepository.findByLastNameStartingWithIgnoreCase(
				"j", PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "lastName")));
		
		final List<User> expectedResult = new ArrayList<>(List.of(user3, user2));
		final List<UserListItem> users = page.getContent();
		
		assertThat(users, notNullValue());
		assertThat(users.size(), equalTo(expectedResult.size()));
		
		for (int i = 0; i < users.size(); i++) {
			assertThat(users.get(i).getId(), equalTo(expectedResult.get(i).getId()));
			assertThat(users.get(i).getFirstName(), equalTo(expectedResult.get(i).getFirstName()));
			assertThat(users.get(i).getLastName(), equalTo(expectedResult.get(i).getLastName()));
		}
	}
	
	@Test
	public void whenExpirePasswordChangeIds_thenUserPasswordResetFieldsAreNulled() {
	  final LocalDateTime now = LocalDateTime.now();
	  final Role role = entityManager.persist(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
	  User user1 = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
	  user1.setPasswordChangeId(UUID.randomUUID());
	  user1.setPasswordChangeIdCreationTime(now.minusDays(3));
	  user1 = entityManager.persist(user1);
	  User user2 = new User("simmyjohnson", "password", "Simmy", "Johnson","simmyJohnson@gmail.com", roles);
    user2.setPasswordChangeId(UUID.randomUUID());
    user2.setPasswordChangeIdCreationTime(now.minusDays(1));
    user2 = entityManager.persistAndFlush(user2);
    
    this.userRepository.expirePasswordChangeIds(now.minusDays(2));
    entityManager.clear();
    
    user1 = this.userRepository.getOne(user1.getId());
    assertThat(user1.getPasswordChangeId(), nullValue());
    assertThat(user1.getPasswordChangeIdCreationTime(),nullValue());
    user2 = this.userRepository.getOne(user2.getId());
    assertThat(user2.getPasswordChangeId(), notNullValue());
    assertThat(user2.getPasswordChangeIdCreationTime(), notNullValue());
	}
}
