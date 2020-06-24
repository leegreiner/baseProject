package edu.duke.rs.baseProject.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;

import edu.duke.rs.baseProject.email.EmailService;
import edu.duke.rs.baseProject.email.MessageType;
import edu.duke.rs.baseProject.event.CreatedEvent;

public class NewSamlUserCreatedEventProcessorUnitTest {
  private static final String URL = "https://localhost";
  private static final String USER_NAME = "johnsmith";
  private static final String USER_EMAIL = "johnsmit@gmail.com";
  @Mock
  private EmailService emailService;
  @Mock
  private MessageSource messageSource;
  private NewSamlUserCreatedEventProcessor processor;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    processor = new NewSamlUserCreatedEventProcessor(emailService, messageSource);
    processor.setApplicationUrl(URL);
  }
  
  @Test
  public void whenEventTriggered_thenEmailIsSent() {
    final String subject = "subject";
    final User user = new User();
    user.setUsername(USER_NAME);
    user.setEmail(USER_EMAIL);
    
    when(messageSource.getMessage(eq(NewSamlUserCreatedEventProcessor.NEW_USER_SUBJECT_CODE), eq(null), any(Locale.class))).thenReturn(subject);
    
    processor.onUserCreated(new CreatedEvent<User>(user));
    
    verify(messageSource, times(1)).getMessage(eq(NewSamlUserCreatedEventProcessor.NEW_USER_SUBJECT_CODE), eq(null), any(Locale.class));
    verify(emailService, times(1)).send(eq(MessageType.NEW_SAML_USER), eq(user.getEmail()),
        eq(subject), ArgumentMatchers.<String, Object>anyMap(), eq(null));
    verifyNoMoreInteractions(messageSource);
    verifyNoMoreInteractions(emailService);
  }
}
