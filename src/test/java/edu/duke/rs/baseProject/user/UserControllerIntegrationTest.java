package edu.duke.rs.baseProject.user;

import static com.spencerwi.hamcrestJDK8Time.matchers.IsAfter.after;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.collection.IsArrayContaining.hasItemInArray;
import static org.junit.Assert.assertThat;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import com.icegreen.greenmail.util.GreenMail;
import com.icegreen.greenmail.util.ServerSetup;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;

public class UserControllerIntegrationTest extends AbstractWebIntegrationTest {
  private GreenMail smtpServer;
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  private Role role;
  
  @Before
  public void init() {
    role = roleRepository.save(new Role(RoleName.USER));
    smtpServer = new GreenMail(new ServerSetup(25, null, "smtp"));
    smtpServer.start();
  }
  
  @After
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
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.ADMINISTRATOR))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USERS_VIEW));
  }
  
  @Test
  public void whenNotAuthenticated_thenUserDetailsRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USER_MAPPING, 1L))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }

  @Test
  public void whenAdministrator_thenUserDetailsReturned() throws Exception { 
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    User user = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    user = userRepository.save(user);
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, user.getId())
      .with(user(UserDetailsBuilder.build(user.getId(), RoleName.ADMINISTRATOR))))
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
      .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.ADMINISTRATOR))))
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
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.ADMINISTRATOR)))
        .param("email", expected.getEmail())
        .param("firstName", expected.getFirstName())
        .param("lastName", expected.getLastName())
        .param("middleInitial", expected.getMiddleInitial())
        .param("roles", expected.getRoles().get(0))
        .param("timeZone", expected.getTimeZone().getID())
        .param("userName", expected.getUserName()))
        .andExpect(status().isFound())
        .andReturn();
    
    final User actual = userRepository.findByUserNameIgnoreCase(expected.getUserName())
        .orElseThrow(() -> new NotFoundException());

    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(actual.getId()).encode().toUriString()));

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
    assertThat(actual.getRoles(), contains(role));
    assertThat(actual.getTimeZone(), equalTo(expected.getTimeZone()));
    assertThat(actual.getUserName(), equalTo(expected.getUserName()));
    assertThat(actual.getVersion(), notNullValue());

    final MimeMessage[] receivedMessages = pollForEmail(smtpServer);
    
    assertThat(1, equalTo(receivedMessages.length));
    final MimeMessage receivedMessage = receivedMessages[0];
    
    assertThat(1, equalTo(receivedMessages.length));
    assertThat((String) receivedMessage.getContent(), containsString(actual.getPasswordChangeId().toString()));
    assertThat(receivedMessage.getAllRecipients(), hasItemInArray(new InternetAddress(actual.getEmail())));
    assertThat(receivedMessage.getSubject(),containsString("DTS"));
    assertThat(receivedMessage.getFrom(), hasItemInArray(new InternetAddress(defaultMailFrom)));
  }
  
  @Test
  public void whenNotAuthenticated_thenUpdateUserRedirectsToLogin() throws Exception {
    this.mockMvc.perform(put(UserController.USER_MAPPING, Long.valueOf(1))
        .with(csrf()))
        .andExpect(status().isFound())
        .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAdministrator_thenUpdateUserUpdatesUser() throws Exception {
    final UserDto expected = buildUserDto();
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    User user = new User(expected.getUserName() + "A", "password", expected.getFirstName() + "B", expected.getLastName() + "C",
         "D" + expected.getEmail(), roles);
    user.setAccountEnabled(false);
    user = userRepository.save(user);
    
    expected.setId(user.getId());
    
    final MvcResult result = this.mockMvc.perform(put(UserController.USER_MAPPING, user.getId())
        .with(csrf())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.ADMINISTRATOR)))
        .param("email", expected.getEmail())
        .param("firstName", expected.getFirstName())
        .param("id", expected.getId().toString())
        .param("lastName", expected.getLastName())
        .param("middleInitial", expected.getMiddleInitial())
        .param("roles", expected.getRoles().get(0))
        .param("timeZone", expected.getTimeZone().getID())
        .param("userName", expected.getUserName()))
        .andExpect(status().isFound())
        .andReturn();
    
    final User actual = userRepository.findById(user.getId())
        .orElseThrow(() -> new NotFoundException());

    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(actual.getId()).encode().toUriString()));

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
    assertThat(actual.getRoles(), contains(role));
    assertThat(actual.getTimeZone(), equalTo(expected.getTimeZone()));
    assertThat(actual.getUserName(), equalTo(user.getUserName()));
    assertThat(actual.getVersion(), equalTo(user.getVersion() + 1));
  }
  
  private UserDto buildUserDto() {
    final UserDto user = UserDto.builder()
        .accountEnabled(true)
        .email("jsmith@gmail.com")
        .firstName("John")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER.name()))
        .timeZone(TimeZone.getTimeZone("Brazil/East"))
        .userName("johnsmith")
        .build();

    return user;
  }
}
