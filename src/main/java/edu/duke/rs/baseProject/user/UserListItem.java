package edu.duke.rs.baseProject.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

public interface UserListItem {
  @Value("#{target.alternateId}")
	UUID getId();
	String getFirstName();
	String getLastName();
	String getUsername();
	String getEmail();
	boolean getAccountEnabled();
}
