package edu.duke.rs.baseProject.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.ConstraintViolationException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.projection.ProjectionFactory;
import org.springframework.data.projection.SpelAwareProxyProjectionFactory;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceUnitTest {
	final static ProjectionFactory PROJECTION_FACTORY = new SpelAwareProxyProjectionFactory();
	@Mock
	private UserRepository userRepository;
	@Autowired
	private UserService userService;
	
	@Test(expected = ConstraintViolationException.class)
	public void whenNullPageablePassedToSearch_thenExceptionThrown() {
		userService.search(null, null);
	}
	
	public void whenNoTermPassedToSearch_thenAllUsersReturned() {
		final List<UserListItem> expectedResult = createUserListItems();
		
		when(userRepository.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<>(expectedResult));
		
		userService.search(null, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "userName")));
		
		verify(userRepository, times(1)).findAllBy(any(Pageable.class));
		verifyNoMoreInteractions(userRepository);
	}
	
	public void whenTermPassedToSearch_thenFindByUserNameStartingWithIgnoreCaseReturned() {
		final String term = "j";
		
		when(userRepository.findAllBy(any(Pageable.class)))
			.thenReturn(new PageImpl<>(createUserListItems()));
		
		userService.search(term, PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "userName")));
		
		verify(userRepository, times(1)).findByUserNameStartingWithIgnoreCase(term, any(Pageable.class));
		verifyNoMoreInteractions(userRepository);
	}
	
	private List<UserListItem> createUserListItems() {
		final List<UserListItem> result = new ArrayList<UserListItem>();
		final Map<String, Object> backingMap = new HashMap<>();
		
		backingMap.put("id", Long.valueOf(1));
		backingMap.put("userName", "jimmyjohnson");
		result.add(PROJECTION_FACTORY.createProjection(UserListItem.class, backingMap));
		
		backingMap.clear();
		backingMap.put("id", Long.valueOf(2));
		backingMap.put("userName", "simmyjohnson");
		result.add(PROJECTION_FACTORY.createProjection(UserListItem.class, backingMap));
		
		return result;
	}
}
