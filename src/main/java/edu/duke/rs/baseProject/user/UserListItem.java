package edu.duke.rs.baseProject.user;

public interface UserListItem {
	Long getId();
	String getFirstName();
	String getLastName();
	String getUserName();
	String getEmail();
	boolean getAccountEnabled();
}
