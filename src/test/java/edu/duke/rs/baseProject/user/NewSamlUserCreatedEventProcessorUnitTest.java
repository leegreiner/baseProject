package edu.duke.rs.baseProject.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.duke.rs.baseProject.email.EmailService;
import edu.duke.rs.baseProject.email.MessageType;
import edu.duke.rs.baseProject.event.CreatedEvent;

public class NewSamlUserCreatedEventProcessorUnitTest {
  private static final String URL = "https://localhost";
  private static final String USER_NAME = "johnsmith";
  private static final String USER_EMAIL = "johnsmit@gmail.com";
  @Mock
  private EmailService emailService;
  private NewSamlUserCreatedEventProcessor processor;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    processor = new NewSamlUserCreatedEventProcessor(emailService);
    processor.setApplicationUrl(URL);
  }
  
  @Test
  public void whenEventTriggered_thenEmailIsSent() {
    final User user = new User();
    user.setUserName(USER_NAME);
    user.setEmail(USER_EMAIL);
    
    processor.onUserCreated(new CreatedEvent<User>(user));
    
    verify(emailService, times(1)).send(eq(MessageType.NEW_SAML_USER), eq(user.getEmail()),
        any(String.class), ArgumentMatchers.<String, Object>anyMap());
    verifyNoMoreInteractions(emailService);
  }
}
