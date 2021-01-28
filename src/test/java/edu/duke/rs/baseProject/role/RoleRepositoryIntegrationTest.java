package edu.duke.rs.baseProject.role;

import static com.github.npathai.hamcrestopt.OptionalMatchers.isPresentAndIs;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.duke.rs.baseProject.AbstractRepositoryTest;

@ExtendWith(SpringExtension.class)
public class RoleRepositoryIntegrationTest extends AbstractRepositoryTest {
	@Autowired
	private RoleRepository roleRepository;
	
	@Test
	public void whenFindByNameFindsRole_thenRoleReturned() {
		final Role expectedRole = testEntityManager.persistAndFlush(new Role(RoleName.USER));
		final Optional<Role> role = roleRepository.findByName(RoleName.USER);
		
		assertThat(role, isPresentAndIs(expectedRole));
	}
}
