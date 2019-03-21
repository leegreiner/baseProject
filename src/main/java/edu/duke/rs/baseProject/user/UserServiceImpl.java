package edu.duke.rs.baseProject.user;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class UserServiceImpl implements UserService {
	private transient final UserRepository userRepository;
	
	public UserServiceImpl(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public Page<UserListItem> search(String term, Pageable pageable) {
		return StringUtils.isBlank(term) ? 
				userRepository.findAllBy(pageable) : 
					userRepository.findByUserNameStartingWithIgnoreCase(term, pageable);
	}

}
