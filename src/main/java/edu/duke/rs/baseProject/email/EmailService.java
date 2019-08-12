package edu.duke.rs.baseProject.email;

import java.util.Collection;
import java.util.Map;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

@Validated
public interface EmailService {
  void send(@NotNull MessageType messageType, @NotBlank String to,
      @NotBlank String subject, Map<String, Object> content);
  
  void send(@NotNull MessageType messageType, @NotEmpty Collection<String> to, Collection<String> cc, 
      Collection<String> bcc, String from, @NotBlank String subject, Map<String, Object> content);
}
