package edu.duke.rs.baseProject.user.passwordReset;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.duke.rs.baseProject.config.ApplicationProperties;
import edu.duke.rs.baseProject.email.EmailService;
import edu.duke.rs.baseProject.email.MessageType;
import edu.duke.rs.baseProject.user.User;

public class PasswordResetInititatedProcessorUnitTest {
  private static final String URL = "https://localhost";
  private static final Long RESET_PASSWORD_EXPIRATION_DAYS = Long.valueOf(2);
  private static final String USER_NAME = "johnsmith";
  private static final String USER_EMAIL = "johnsmit@gmail.com";
  @Mock
  private EmailService emailService;
  private PasswordResetInititatedProcessor processor;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    final ApplicationProperties applicationProperties = new ApplicationProperties();
    applicationProperties.setUrl(URL);
    applicationProperties.getSecurity().getPassword().setResetPasswordExpirationDays(RESET_PASSWORD_EXPIRATION_DAYS);
    processor = new PasswordResetInititatedProcessor(emailService, applicationProperties);
  }
  
  @Test
  public void whenEventTriggered_thenEmailIsSent() {
    final User user = new User();
    user.setUsername(USER_NAME);
    user.setEmail(USER_EMAIL);
    user.setPasswordChangeId(UUID.randomUUID());
    
    processor.onPasswordResetInitiated(new PasswordResetInitiatedEvent(this, user));
    
    verify(emailService, times(1)).send(eq(MessageType.PASSWORD_RESET_INITIATED), eq(user.getEmail()),
        any(String.class), ArgumentMatchers.<String, Object>anyMap(), eq(null));
    verifyNoMoreInteractions(emailService);
  }
}
