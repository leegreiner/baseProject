package edu.duke.rs.baseProject.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Abbreviated user details")
public interface UserListItem {
  @Value("#{target.alternateId}")
  @Schema(description = "User's external unique identifier")
	UUID getId();
  @Schema(description = "User's first name")
	String getFirstName();
  @Schema(description = "User's last name")
	String getLastName();
  @Schema(description = "User's username")
	String getUsername();
  @Schema(description = "User's email address")
	String getEmail();
  @Schema(description = "Indicator of whether the user's account is enabled and can login to the system")
	boolean getAccountEnabled();
}
