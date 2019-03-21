package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class UserRepositoryIntegrationTest {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private UserRepository userRepository;
	
	@Test
	public void whenFindByUserNameStartingWithIgnoreCaseNoUsers_thenReturnNoUsers() {
		final Page<UserListItem> page = userRepository.findByUserNameStartingWithIgnoreCase(
				"j", PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "userName")));
		
		final List<UserListItem> users = page.getContent();
		
		assertThat(users, notNullValue());
		assertThat(users.size(), equalTo(0));
	}
	
	@Test
	public void whenFindByUserNameStartingWithIgnoreCaseUsers_thenReturnUsers() {
		User user1 = new User("jimmystevens");
		user1 = entityManager.persist(user1);
		User user2 = new User("jimmyjohnson");
		user2 = entityManager.persist(user2);
		User user3 = new User("simmyjohnson");
		user3 = entityManager.persistAndFlush(user3);
		
		final Page<UserListItem> page = userRepository.findByUserNameStartingWithIgnoreCase(
				"j", PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "userName")));
		
		final List<User> expectedResult = new ArrayList<>(List.of(user2, user1));
		final List<UserListItem> users = page.getContent();
		
		assertThat(users, notNullValue());
		assertThat(users.size(), equalTo(expectedResult.size()));
		
		for (int i = 0; i < users.size(); i++) {
			assertThat(users.get(i).getId(), equalTo(expectedResult.get(i).getId()));
			assertThat(users.get(i).getUserName(), equalTo(expectedResult.get(i).getUserName()));
		}
	}
}
