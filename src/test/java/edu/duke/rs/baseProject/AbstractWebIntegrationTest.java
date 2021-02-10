package edu.duke.rs.baseProject;

import javax.mail.internet.MimeMessage;

import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.icegreen.greenmail.util.GreenMail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@Sql("/dbdata/importRolesPrivileges.sql")
public abstract class AbstractWebIntegrationTest extends AbstractWebTest {
  private static final int EMAIL_POLL_ATTEMPTS = 10;
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  private TestUtilsService testUtilsService;
  @Value("${app.defaultEmailFrom}")
  protected String defaultMailFrom;
  @Value("${spring.mail.port}")
  protected Integer mailPort;
  
  @AfterEach
  public void resetState() throws Exception {
    this.testUtilsService.resetState();
  }
  
  protected static MimeMessage[] pollForEmail(final GreenMail smtpServer) {
    MimeMessage[] receivedMessages = null;
    
    for (int i = 0; i < EMAIL_POLL_ATTEMPTS; i++) {
      receivedMessages = smtpServer.getReceivedMessages();
      
      if (receivedMessages.length == 0) {
        try {
          Thread.sleep(1100);
        } catch (final InterruptedException te) {}
      }
    }
    
    return receivedMessages;
  }
}
