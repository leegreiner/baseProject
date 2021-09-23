package edu.duke.rs.baseProject.user.passwordReset;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.ArrayMatching.hasItemInArray;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.EmailRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.PersistentUserBuilder;
import edu.duke.rs.baseProject.index.IndexController;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.user.User;
import edu.duke.rs.baseProject.user.UserRepository;

public class PasswordResetControllerIntegrationTest extends AbstractWebIntegrationTest {
  private static final String USER_PASSWORD =
      new EasyRandom(new EasyRandomParameters().seed(new Random().nextLong()).stringLengthRange(10, 15)).nextObject(String.class);
  private static final EmailRandomizer EMAIL_RANDOMIZER = new EmailRandomizer(new Random().nextLong());
  private GreenMail smtpServer;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PasswordEncoder passwordEncoder;
  @Autowired
  private PersistentUserBuilder persistentUserBuilder;
  
  @BeforeEach
  public void init() {
    smtpServer = new GreenMail(new ServerSetup(mailPort, null, "smtp"));
    smtpServer.start();
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    smtpServer.stop();
  }
  
  @Test
  public void whenPasswordResetInitiated_thenResetPasswordFieldsSetAndEmailSent() throws Exception {
    final User user = createUser();
    
    final MvcResult result = this.mockMvc.perform(post(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("email", user.getEmail()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(IndexController.INDEX_MAPPING).toUriString()));
    
    final User updatedUser = this.userRepository.findById(user.getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    assertThat(updatedUser.getPasswordChangeId(), notNullValue());
    assertThat(updatedUser.getPasswordChangeIdCreationTime(), notNullValue());
    
    final MimeMessage[] receivedMessages = pollForEmail(smtpServer);
    
    assertThat(receivedMessages.length, equalTo(1));
    final MimeMessage receivedMessage = receivedMessages[0];
    
    assertThat((String) receivedMessage.getContent(), containsString(updatedUser.getPasswordChangeId().toString()));
    assertThat(receivedMessage.getAllRecipients(), hasItemInArray(new InternetAddress(updatedUser.getEmail())));
    assertThat(receivedMessage.getSubject(),containsString("DTS"));
    assertThat(receivedMessage.getFrom(), hasItemInArray(new InternetAddress(defaultMailFrom)));
  }
  
  @Test
  public void whenPasswordReset_thenResetPasswordFieldsCleared() throws Exception {
    User user = createUser();
    user.setPasswordChangeId(UUID.randomUUID());
    user.setPasswordChangeIdCreationTime(LocalDateTime.now());
    user = userRepository.save(user);
    
    final MvcResult result = this.mockMvc.perform(put(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("passwordChangeId", user.getPasswordChangeId().toString())
        .param("username", user.getUsername())
        .param("password", USER_PASSWORD + "A")
        .param("confirmPassword", USER_PASSWORD + "A"))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(IndexController.INDEX_MAPPING).toUriString()));
    
    final User updatedUser = this.userRepository.findById(user.getId())
        .orElseThrow(() -> new IllegalArgumentException("User not found"));

    assertThat(updatedUser.getPasswordChangeId(), nullValue());
    assertThat(updatedUser.getPasswordChangeIdCreationTime(), nullValue());
    assertThat(updatedUser.getLastPasswordChange(), notNullValue());
    assertThat(passwordEncoder.matches(USER_PASSWORD + "A", updatedUser.getPassword()), equalTo(true));
  }
  
  private User createUser() {
    return this.persistentUserBuilder.build(easyRandom.nextObject(String.class), USER_PASSWORD,
        easyRandom.nextObject(String.class), easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.USER));
  }
}
