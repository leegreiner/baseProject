package edu.duke.rs.baseProject.user;

import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
	private transient final UserRepository userRepository;
	
	public UserServiceImpl(final UserRepository userRepository) {
		this.userRepository = userRepository;
	}
}
