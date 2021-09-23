package edu.duke.rs.baseProject.user;

import static com.spencerwi.hamcrestJDK8Time.matchers.IsAfter.after;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.ArrayMatching.hasItemInArray;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.ZoneId;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.hibernate.envers.RevisionType;
import org.jeasy.random.EasyRandom;
import org.jeasy.random.EasyRandomParameters;
import org.jeasy.random.randomizers.EmailRandomizer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.PersistentUserBuilder;
import edu.duke.rs.baseProject.PersistentUserDetailsBuilder;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.user.history.UserHistory;

public class UserControllerIntegrationTest extends AbstractWebIntegrationTest {
  private static final EmailRandomizer EMAIL_RANDOMIZER = new EmailRandomizer(new Random().nextLong());
  private GreenMail smtpServer;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private PersistentUserDetailsBuilder persistentUserDetailsBuilder;
  @Autowired
  private PersistentUserBuilder persistentUserBuilder;
  private EasyRandom easyRandom;
  
  @BeforeEach
  public void init() {
    smtpServer = new GreenMail(new ServerSetup(mailPort, null, "smtp"));
    smtpServer.start();
    easyRandom = new EasyRandom(new EasyRandomParameters().seed(new Random().nextLong()).stringLengthRange(8,10));
  }
  
  @AfterEach
  public void tearDown() throws Exception {
    smtpServer.stop();
  }
  
  @Test
  public void whenNotAuthenticated_thenUsersRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)).andExpect(status().isFound())
        .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAdminitrator_thenUsersReturned() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)
        .with(user(persistentUserDetailsBuilder.build(easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(),
            Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USERS_VIEW));
  }
  
  @Test
  public void whenNotAuthenticated_thenUserDetailsRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USER_MAPPING, UUID.randomUUID()))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }

  @Test
  public void whenAdministrator_thenUserDetailsReturned() throws Exception { 
    final User user = persistentUserBuilder.build("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", Set.of(RoleName.USER));
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, user.getAlternateId())
      .with(user(persistentUserDetailsBuilder.build(easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(),
          Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USER_DETAILS_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, equalTo(user)));
  }
  
  @Test
  public void whenNotAuthenticated_thenNewUserDisplayRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)
        .param(UserController.ACTION_REQUEST_PARAM, "new"))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAdministrator_thenNewUserDisplayReturned() throws Exception {    
    this.mockMvc.perform(get(UserController.USERS_MAPPING)
        .param(UserController.ACTION_REQUEST_PARAM, "new")
      .with(user(persistentUserDetailsBuilder.build(easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(),
          Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.NEW_USER_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
      .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())));
  }
  
  @Test
  public void whenNotAuthenticated_thenNewUserRedirectsToLogin() throws Exception {
    this.mockMvc.perform(post(UserController.USERS_MAPPING)
        .with(csrf()))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAdministrator_thenNewUserCreatesUser() throws Exception {
    final UserDto expected = buildUserDto();
    
    final MvcResult result = this.mockMvc.perform(post(UserController.USERS_MAPPING)
        .with(csrf())
        .with(user(persistentUserDetailsBuilder.build(easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(),
            Set.of(RoleName.ADMINISTRATOR))))
        .param("email", expected.getEmail())
        .param("firstName", expected.getFirstName())
        .param("lastName", expected.getLastName())
        .param("middleInitial", expected.getMiddleInitial())
        .param("roles", expected.getRoles().get(0).name())
        .param("timeZone", expected.getTimeZone().getID())
        .param("username", expected.getUsername()))
        .andExpect(status().isFound())
        .andReturn();
    
    final User actual = userRepository.findByUsernameIgnoreCase(expected.getUsername())
        .orElseThrow(() -> new NotFoundException());

    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(actual.getAlternateId()).encode().toUriString()));

    assertThat(actual.getCreatedDate(), notNullValue());
    assertThat(actual.getDisplayName(),
        equalTo(expected.getFirstName() + ' ' + expected.getMiddleInitial() + ' ' + expected.getLastName()));
    assertThat(actual.getEmail(), equalTo(expected.getEmail()));
    assertThat(actual.getFirstName(), equalTo(expected.getFirstName()));
    assertThat(actual.getId(), notNullValue());
    assertThat(actual.getLastLoggedIn(), nullValue());
    assertThat(actual.getLastModifiedDate(), notNullValue());
    assertThat(actual.getLastName(), equalTo(expected.getLastName()));
    assertThat(actual.getMiddleInitial(), equalTo(expected.getMiddleInitial()));
    assertThat(actual.getPassword(), notNullValue());
    actual.getRoles().forEach(role -> assertThat(expected.getRoles(), contains(role.getName())));
    assertThat(actual.getTimeZone(), equalTo(expected.getTimeZone()));
    assertThat(actual.getUsername(), equalTo(expected.getUsername()));
    assertThat(actual.getVersion(), notNullValue());

    final MimeMessage[] receivedMessages = pollForEmail(smtpServer);
    
    assertThat(1, equalTo(receivedMessages.length));
    final MimeMessage receivedMessage = receivedMessages[0];
    
    assertThat(1, equalTo(receivedMessages.length));
    assertThat((String) receivedMessage.getContent(), containsString(actual.getPasswordChangeId().toString()));
    assertThat(receivedMessage.getAllRecipients(), hasItemInArray(new InternetAddress(actual.getEmail())));
    assertThat(receivedMessage.getSubject(), containsString("DTS"));
    assertThat(receivedMessage.getFrom(), hasItemInArray(new InternetAddress(defaultMailFrom)));
  }
  
  @Test
  public void whenNotAuthenticated_thenUpdateUserRedirectsToLogin() throws Exception {
    this.mockMvc.perform(put(UserController.USER_MAPPING, UUID.randomUUID())
        .with(csrf()))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAdministrator_thenUpdateUserUpdatesUser() throws Exception {
    final String password = easyRandom.nextObject(String.class);
    final UserDto expected = buildUserDto();
    User user = persistentUserBuilder.build(expected.getUsername() + "A", password, expected.getFirstName() + "B", expected.getLastName() + "C",
         "D" + expected.getEmail(), Set.of(RoleName.USER));
    user.setAccountEnabled(false);
    user = userRepository.save(user);
    
    expected.setId(user.getAlternateId());
    
    final MvcResult result = this.mockMvc.perform(put(UserController.USER_MAPPING, user.getAlternateId())
        .with(csrf())
        .with(user(persistentUserDetailsBuilder.build(easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(),
            Set.of(RoleName.ADMINISTRATOR))))
        .param("email", expected.getEmail())
        .param("firstName", expected.getFirstName())
        .param("id", expected.getId().toString())
        .param("lastName", expected.getLastName())
        .param("middleInitial", expected.getMiddleInitial())
        .param("roles", expected.getRoles().get(0).name())
        .param("timeZone", expected.getTimeZone().getID())
        .param("username", expected.getUsername())
        .param("reasonForChange", easyRandom.nextObject(String.class))
        .param("password", PersistentUserDetailsBuilder.PASSWORD))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .encode().buildAndExpand(expected.getId()).toString()))
        .andReturn();
    
    final User actual = userRepository.findByAlternateId(user.getAlternateId(), UserConstants.USER_AND_ROLES_ENTITY_GRAPH)
        .orElseThrow(() -> new NotFoundException());

    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(actual.getAlternateId()).encode().toUriString()));

    assertThat(actual.getCreatedDate().atZone(ZoneId.systemDefault()).toEpochSecond(),
        equalTo(user.getCreatedDate().atZone(ZoneId.systemDefault()).toEpochSecond()));
    assertThat(actual.getDisplayName(),
        equalTo(expected.getFirstName() + ' ' + expected.getMiddleInitial() + ' ' + expected.getLastName()));
    assertThat(actual.getEmail(), equalTo(expected.getEmail()));
    assertThat(actual.getFirstName(), equalTo(expected.getFirstName()));
    assertThat(actual.getLastLoggedIn(), nullValue());
    assertThat(actual.getLastModifiedDate(), after(user.getLastModifiedDate()));
    assertThat(actual.getLastName(), equalTo(expected.getLastName()));
    assertThat(actual.getMiddleInitial(), equalTo(expected.getMiddleInitial()));
    assertThat(actual.getPassword(), equalTo(user.getPassword()));
    actual.getRoles().forEach(role -> assertThat(expected.getRoles(), contains(role.getName())));
    assertThat(actual.getTimeZone(), equalTo(expected.getTimeZone()));
    assertThat(actual.getUsername(), equalTo(user.getUsername()));
    assertThat(actual.getVersion(), equalTo(user.getVersion() + 1));
  }

  @Test
  public void whenUserCreated_thenRecordIsAddedToHistory() throws Exception {
    final UserDto expected = buildUserDto();
    final AppPrincipal userDetails = (AppPrincipal) persistentUserDetailsBuilder
        .build(easyRandom.nextObject(String.class), EMAIL_RANDOMIZER.getRandomValue(),
            Set.of(RoleName.ADMINISTRATOR));
    
    this.mockMvc.perform(post(UserController.USERS_MAPPING)
        .with(csrf())
        .with(user(userDetails))
        .param("email", expected.getEmail())
        .param("firstName", expected.getFirstName())
        .param("lastName", expected.getLastName())
        .param("middleInitial", expected.getMiddleInitial())
        .param("roles", expected.getRoles().get(0).name())
        .param("timeZone", expected.getTimeZone().getID())
        .param("username", expected.getUsername()))
        .andExpect(status().isFound());
    
    final User actual = userRepository.findByUsernameIgnoreCase(expected.getUsername())
        .orElseThrow(() -> new NotFoundException());
    
    final MvcResult result = this.mockMvc.perform(get(UserController.USER_HISTORY_MAPPING, actual.getAlternateId())
        .with(user(userDetails)))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USER_HISTORY_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, equalTo(actual)))
      .andExpect(model().attribute(UserController.USER_HISTORY_MODEL_ATTRIBUTE, notNullValue()))
      .andReturn();
    
    @SuppressWarnings("unchecked")
    final List<UserHistory> histories = (List<UserHistory>) result.getModelAndView().getModel().get(UserController.USER_HISTORY_MODEL_ATTRIBUTE);
    assertThat(histories.size(), equalTo(1));
    final UserHistory history = histories.get(0);
    assertThat(history.getEventTime(), notNullValue());
    assertThat(history.getInitiator(), equalTo(userDetails.getDisplayName()));
    assertThat(history.getRevision(), notNullValue());
    assertThat(history.getRevisionType(), equalTo(RevisionType.ADD));
    final User actualHistoryUser = history.getUser();
    assertThat(actualHistoryUser, notNullValue());
    assertThat(actualHistoryUser.getDisplayName(), equalTo(actual.getDisplayName()));
    assertThat(actualHistoryUser.getEmail(), equalTo(actual.getEmail()));
    assertThat(actualHistoryUser.getFirstName(), equalTo(actual.getFirstName()));
    assertThat(actualHistoryUser.getId(), equalTo(actual.getId()));
    assertThat(actualHistoryUser.getLastLoggedIn(), equalTo(actual.getLastLoggedIn()));
    assertThat(actualHistoryUser.getLastName(), equalTo(actual.getLastName()));
    assertThat(actualHistoryUser.getLastPasswordChange(), equalTo(actual.getLastPasswordChange()));
    assertThat(actualHistoryUser.getMiddleInitial(), equalTo(actual.getMiddleInitial()));
    assertThat(actualHistoryUser.getPasswordChangeId(), equalTo(actual.getPasswordChangeId()));
    assertThat(actualHistoryUser.getPasswordChangeIdCreationTime(), equalTo(actual.getPasswordChangeIdCreationTime()));
    assertThat(actualHistoryUser.getTimeZone(), equalTo(actual.getTimeZone()));
    assertThat(actualHistoryUser.getUsername(), equalTo(actual.getUsername()));
    assertThat(actualHistoryUser.isAccountEnabled(), equalTo(actual.isAccountEnabled()));
    assertThat(actualHistoryUser.getRoles().size(), equalTo(actual.getRoles().size()));
  }
  
  private UserDto buildUserDto() {
    final UserDto user = UserDto.builder()
        .accountEnabled(true)
        .email("jsmith@gmail.com")
        .firstName(easyRandom.nextObject(String.class))
        .lastName(easyRandom.nextObject(String.class))
        .middleInitial("M")
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getTimeZone("Brazil/East"))
        .username(easyRandom.nextObject(String.class))
        .id(UUID.randomUUID())
        .build();

    return user;
  }
}
