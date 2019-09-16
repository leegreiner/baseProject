

import javax.mail.internet.MimeMessage;

import org.junit.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

import com.icegreen.greenmail.util.GreenMail;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
public abstract class AbstractWebIntegrationTest extends AbstractWebTest {
  private static final int EMAIL_POLL_ATTEMPTS = 10;
  @Autowired
  protected MockMvc mockMvc;
  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired(required = false)
  private CacheManager cacheManager;
  @Value("${app.defaultEmailFrom}")
  protected String defaultMailFrom;
  
  
  @Before
  public void resetState() throws Exception {
    TestUtils.resetState(jdbcTemplate, cacheManager);
  }
  
  protected static MimeMessage[] pollForEmail(final GreenMail smtpServer) {
    MimeMessage[] receivedMessages = null;
    
    for (int i = 0; i < EMAIL_POLL_ATTEMPTS; i++) {
      receivedMessages = smtpServer.getReceivedMessages();
      
      if (receivedMessages.length == 0) {
        try {
          Thread.sleep(1000);
        } catch (final InterruptedException te) {}
      }
    }
    
    return receivedMessages;
  }
}
