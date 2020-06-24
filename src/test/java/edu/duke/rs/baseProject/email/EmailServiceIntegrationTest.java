package edu.duke.rs.baseProject.email;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.ArrayMatching.hasItemInArray;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIn.in;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.Address;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.mail.util.MimeMessageParser;
import org.assertj.core.util.Arrays;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.util.ResourceUtils;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;

public class EmailServiceIntegrationTest extends AbstractWebIntegrationTest {
  private GreenMail smtpServer;
  @Autowired
  private EmailService emailService;
  @Value("${app.defaultEmailFrom}")
  private String defaultMailFrom;
  
  @BeforeEach
  public void setUp() throws Exception {
    smtpServer = new GreenMail(new ServerSetup(mailPort, null, "smtp"));
    smtpServer.start();
  }

  @AfterEach
  public void tearDown() throws Exception {
    smtpServer.stop();
  }
  
  @Test
  public void whenValidEmailPassed_thenEmailSent() throws Exception {
    final String expectedContent = "This is a test";
    final String expectedTo = "abc@123.com";
    final String expectedSubject = "Test subject";
    final Map<String, Object> content = new HashMap<String, Object>();
    content.put("message", expectedContent);
    
    emailService.send(MessageType.TEST, expectedTo, expectedSubject, content, null);
    
    final MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
    assertThat(1, equalTo(receivedMessages.length));
    final MimeMessage receivedMessage = receivedMessages[0];
    
    assertThat((String) receivedMessage.getContent(), containsString(expectedContent));
    assertThat(receivedMessage.getAllRecipients(), hasItemInArray(new InternetAddress(expectedTo)));
    assertThat(receivedMessage.getSubject(),equalTo(expectedSubject));
    assertThat(receivedMessage.getFrom(), hasItemInArray(new InternetAddress(defaultMailFrom)));
  }
  
  @Test
  public void whenValidEmailAndAttachmentPassed_thenEmailAndAttachmentSent() throws Exception {
    final String expectedContent = "This is a test";
    final String expectedTo = "abc@123.com";
    final String expectedSubject = "Test subject";
    final List<File> attachments = List.of(ResourceUtils.getFile("classpath:files/twoXmlFiles.zip"));
    final Map<String, Object> content = new HashMap<String, Object>();
    content.put("message", expectedContent);
    
    emailService.send(MessageType.TEST, expectedTo, expectedSubject, content, attachments);
    
    final MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
    assertThat(1, equalTo(receivedMessages.length));
    final MimeMessage receivedMessage = receivedMessages[0];
    
    assertThat(receivedMessage.getAllRecipients(), hasItemInArray(new InternetAddress(expectedTo)));
    assertThat(receivedMessage.getSubject(),equalTo(expectedSubject));
    assertThat(receivedMessage.getFrom(), hasItemInArray(new InternetAddress(defaultMailFrom)));
    assertThat(receivedMessage.getContentType(), startsWith(MediaType.MULTIPART_MIXED_VALUE));
    
    final MimeMessageParser mimeMessage = new MimeMessageParser(receivedMessage);
    
    mimeMessage.parse();
    
    assertThat(mimeMessage.getHtmlContent(), containsString(expectedContent));
    assertThat(mimeMessage.getAttachmentList(),  hasSize(attachments.size()));
    assertThat(mimeMessage.getAttachmentList().get(0).getName(), equalTo(attachments.get(0).getName()));
  }
  
  @Test
  public void whenUnableToSendEmail_thenEmailExceptionThrown() {
    smtpServer.stop();
    
    assertThrows(EmailException.class, () -> emailService.send(MessageType.TEST, "abc@123", "Test subject", Collections.emptyMap(), null));
  }
  
  @Test
  public void whenValidEmailPassed_thenEmailSentLong() throws Exception {
    final String expectedContent = "This is a test";
    final List<String> expectedTo = List.of("abc@123.com", "def.com");
    final String expectedSubject = "Test subject";
    final List<String> expectedCc = List.of("ghi@123.com", "jkl@123.com");
    final List<String> expectedBcc = List.of("mno@123.com", "pqr@123.com");
    final String expectedFrom = "stu@123.com";
    final Map<String, Object> content = new HashMap<String, Object>();
    content.put("message", expectedContent);
    
    emailService.send(MessageType.TEST, expectedTo, expectedCc, expectedBcc, expectedFrom, expectedSubject, content, null);
    
    final MimeMessage[] receivedMessages = smtpServer.getReceivedMessages();
    assertThat(receivedMessages.length, equalTo(expectedTo.size() + expectedCc.size() + expectedBcc.size()));
    final MimeMessage receivedMessage = receivedMessages[0];
    
    final List<Address> allRecipients = new ArrayList<Address>(expectedTo.size() + expectedCc.size());
    
    expectedTo.stream().forEach(r -> {
      try {allRecipients.add(new InternetAddress(r)); } catch (AddressException e) {}
    });
    expectedCc.stream().forEach(r -> {
      try {allRecipients.add(new InternetAddress(r)); } catch (AddressException e) {}
    });
    
    assertThat((String) receivedMessage.getContent(), containsString(expectedContent));
    for (final Address recipient : allRecipients) {
      assertThat(recipient, in(Arrays.asList(receivedMessage.getAllRecipients())));
    }
    assertThat(receivedMessage.getSubject(),equalTo(expectedSubject));
    assertThat(receivedMessage.getFrom().length, equalTo(1));
    assertThat(receivedMessage.getFrom(), hasItemInArray(new InternetAddress(expectedFrom)));
  }
  
  @Test
  public void whenUnableToSendEmail_thenEmailExceptionThrownLong() {
    smtpServer.stop();
    
    assertThrows(EmailException.class, () -> emailService.send(MessageType.TEST, List.of("abc@123"), List.of("def@123.com"),
        List.of("ghi@123.com"), "jkl@123.com", "Test subject", Collections.emptyMap(), null));
  }
}
