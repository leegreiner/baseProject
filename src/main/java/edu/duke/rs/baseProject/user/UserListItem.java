package edu.duke.rs.baseProject.user;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel(description = "Abbreviated user details")
public interface UserListItem {
  @Value("#{target.alternateId}")
  @ApiModelProperty(notes = "User's external unique identifier")
	UUID getId();
  @ApiModelProperty(notes = "User's first name")
	String getFirstName();
  @ApiModelProperty(notes = "User's last name")
	String getLastName();
  @ApiModelProperty(notes = "User's username")
	String getUsername();
  @ApiModelProperty(notes = "User's email address")
	String getEmail();
  @ApiModelProperty(notes = "Indicator of whether the user's account is enabled and can login to the system")
	boolean getAccountEnabled();
}
