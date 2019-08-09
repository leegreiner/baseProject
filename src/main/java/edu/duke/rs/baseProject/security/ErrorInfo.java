package edu.duke.rs.baseProject.security;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class ErrorInfo {
  @NonNull
	private String message;
  @NonNull
	private String url;
}
