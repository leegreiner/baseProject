package edu.duke.rs.baseProject.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

public class EmailServiceUnitTest {
  @Mock
  private JavaMailSender mailSender;
  @Mock
  private MessageContentBuilder contentBuilder;
  private EmailService emailService;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    emailService = new EmailServiceImpl(mailSender, contentBuilder);
  }
  
  @Test(expected = EmailException.class)
  public void whenEmailIsNotAbleToBeSent_thenEmailExceptionThrown() {
   final Map<String, Object> content = new HashMap<String, Object>();
   content.put("message", "This is a test");
   when(contentBuilder.build(MessageType.TEST, content)).thenReturn("This is a test");
   doThrow(EmailException.class).when(mailSender).send(any(MimeMessagePreparator.class));
   
   emailService.send(MessageType.TEST, "abc@123.com", "Test subject", content);
  }
  
  @Test
  public void whenValidEmailRequest_thenEmailSent() {
   final Map<String, Object> content = new HashMap<String, Object>();
   content.put("message", "This is a test");
   when(contentBuilder.build(MessageType.TEST, content)).thenReturn("This is a test");
   
   emailService.send(MessageType.TEST, "abc@123.com", "Test subject", content);
   
   verifyNoMoreInteractions(contentBuilder);
   verify(mailSender, times(1)).send(any(MimeMessagePreparator.class));
   verifyNoMoreInteractions(mailSender);
  }
}
