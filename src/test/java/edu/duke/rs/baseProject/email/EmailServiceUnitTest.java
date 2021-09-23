package edu.duke.rs.baseProject.email;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;

import edu.duke.rs.baseProject.AbstractBaseTest;

public class EmailServiceUnitTest extends AbstractBaseTest {
  @Mock
  private JavaMailSender mailSender;
  @Mock
  private MessageContentBuilder contentBuilder;
  private EmailService emailService;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
    emailService = new EmailServiceImpl(mailSender, contentBuilder);
  }
  
  @Test
  public void whenEmailIsNotAbleToBeSent_thenEmailExceptionThrown() {
   final Map<String, Object> content = new HashMap<String, Object>();
   content.put("message", easyRandom.nextObject(String.class));
   when(contentBuilder.build(MessageType.TEST, content)).thenReturn(easyRandom.nextObject(String.class));
   doThrow(EmailException.class).when(mailSender).send(any(MimeMessagePreparator.class));
   
   assertThrows(EmailException.class, () -> emailService.send(MessageType.TEST, "abc@123.com", easyRandom.nextObject(String.class), content, null),
       "error.unableToSendEmail");
  }
  
  @Test
  public void whenValidEmailRequestWithContent_thenEmailSent() {
   final Map<String, Object> content = new HashMap<String, Object>();
   content.put("message", easyRandom.nextObject(String.class));
   when(contentBuilder.build(MessageType.TEST, content)).thenReturn(easyRandom.nextObject(String.class));
   
   emailService.send(MessageType.TEST, "abc@123.com", easyRandom.nextObject(String.class), content, null);
   
   verifyNoMoreInteractions(contentBuilder);
   verify(mailSender, times(1)).send(any(MimeMessagePreparator.class));
   verifyNoMoreInteractions(mailSender);
  }
  
  @Test
  public void whenValidEmailRequestWithoutContent_thenEmailSent() {
   final Map<String, Object> content = new HashMap<String, Object>();
   content.put("message", easyRandom.nextObject(String.class));
   when(contentBuilder.build(MessageType.TEST, content)).thenReturn(easyRandom.nextObject(String.class));
   
   emailService.send(MessageType.TEST, "abc@123.com", easyRandom.nextObject(String.class), null, null);
   
   verifyNoMoreInteractions(contentBuilder);
   verify(mailSender, times(1)).send(any(MimeMessagePreparator.class));
   verifyNoMoreInteractions(mailSender);
  }
}
