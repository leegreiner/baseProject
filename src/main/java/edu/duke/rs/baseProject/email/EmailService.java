package edu.duke.rs.baseProject.email;

import java.io.File;
import java.util.Collection;
import java.util.Map;

import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Validated
public interface EmailService {
  void send(@NotNull MessageType messageType, @NotBlank String to,
      @NotBlank String subject, Map<String, Object> content, Collection<File> attachments);
  
  void send(@NotNull MessageType messageType, @NotEmpty Collection<String> to, Collection<String> cc, 
      Collection<String> bcc, String from, @NotBlank String subject, Map<String, Object> content,
      Collection<File> attachments);
}

