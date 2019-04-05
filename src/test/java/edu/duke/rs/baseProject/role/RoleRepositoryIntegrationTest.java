package edu.duke.rs.baseProject.role;

import static org.hamcrest.MatcherAssert.assertThat;
import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@DataJpaTest
public class RoleRepositoryIntegrationTest {
	@Autowired
	private TestEntityManager entityManager;
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	public void whenFindByNameFindsRole_thenRoleReturned() {
		final Role expectedRole = entityManager.persistAndFlush(new Role(RoleName.USER));
		final Optional<Role> role = roleRepository.findByName(RoleName.USER);
		
		assertThat(role, isPresentAndIs(expectedRole));
	}
}
