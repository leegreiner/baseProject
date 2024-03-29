package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.flash;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;
import java.util.TimeZone;
import java.util.UUID;

import org.jeasy.random.randomizers.EmailRandomizer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.web.util.UriComponentsBuilder;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.error.ExceptionController;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.PersistentSecurityUtils;
import edu.duke.rs.baseProject.user.history.UserHistory;
import edu.duke.rs.baseProject.user.history.UserHistoryRepository;

@WebMvcTest(UserController.class)
public class UserControllerUnitTest extends AbstractWebUnitTest {
  private static final EmailRandomizer EMAIL_RANDOMIZER = new EmailRandomizer(new Random().nextLong());
  @MockBean
  private UserService userService;
  @MockBean
  private UserHistoryRepository userHistoryRepository;
  @MockBean
  private PersistentSecurityUtils persistentSecurityUtils;
  @Autowired
  private MessageSource messageSource;
  private UserDetailsBuilder userDetailsBuilder = new UserDetailsBuilder();
  
  @Test
  public void whenNotAuthenticated_thenUsersRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenNotAdministrator_thenUsersRedirectsToErrorPage() throws Exception {
    final UserDto expected = buildUserDto();
    
    for (int i = 0; i < RoleName.values().length; i++) {
      final RoleName roleName = RoleName.values()[i];
      
      if (roleName != RoleName.ADMINISTRATOR) {
        final MvcResult result = this.mockMvc.perform(post(UserController.USERS_MAPPING)
            .with(csrf())
            .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(roleName))))
            .param("email", expected.getEmail())
            .param("firstName", expected.getFirstName())
            .param("lastName", expected.getLastName())
            .param("middleInitial", expected.getMiddleInitial())
            .param("roles", expected.getRoles().get(0).name())
            .param("timeZone", expected.getTimeZone().getID())
            .param("username", expected.getUsername()))
            .andExpect(status().isFound())
            .andReturn();
        
        assertThat(result.getResponse().getRedirectedUrl(), equalTo(NOT_AUTORIZED_MAPPING));
      }
    }
  }

  @Test
  public void whenAdministrator_thenUsersPagePresented() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USERS_VIEW));
  }
  
  @Test
  public void whenNotAuthenticated_thenUserDetailsRedirectToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USER_MAPPING, 1L))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenNotAdminitrator_thenUserDetailsRedirectsErrorPage() throws Exception {
    for (int i = 0; i < RoleName.values().length; i++) {
      final RoleName roleName = RoleName.values()[i];
      
      if (roleName != RoleName.ADMINISTRATOR) {
        final MvcResult result = this.mockMvc.perform(get(UserController.USER_MAPPING, 1L)
            .with(user(userDetailsBuilder.build(1L, EMAIL_RANDOMIZER.getRandomValue(), Set.of(roleName)))))
        .andExpect(status().isFound())
        .andReturn();
    
        assertThat(result.getResponse().getRedirectedUrl(), equalTo(NOT_AUTORIZED_MAPPING));
      }
    }
  }
  
  @Test
  public void whenAuthenticatedButRequestingNonexistantUser_thenExceptionErrorPageReturned() throws Exception {
    final UUID userId = UUID.randomUUID();
    when(userService.getUser(userId)).thenThrow(new NotFoundException("error.principalNotFound", (Object[])null));
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, userId)
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(ExceptionController.EXCEPTION_ERROR_VIEW))
      .andExpect(model().attribute(ExceptionController.EXCEPTION_MESSAGE_ATTRIBUTE, notNullValue()));
    
    verify(userService, times(1)).getUser(userId);
    verifyNoMoreInteractions(userService);
  }
  
  @Test
  public void whenAuthenticated_thenUserDetailsReturned() throws Exception {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUsername("abc");
    when(userService.getUser(user.getAlternateId())).thenReturn(user);
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, user.getAlternateId())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USER_DETAILS_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, equalTo(user)));
    
    verify(userService, times(1)).getUser(user.getAlternateId());
    verifyNoMoreInteractions(userService);
  }
  
  @Test
  public void whenUserDetailsThrowsApplicationException_thenErrorViewReturned() throws Exception {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUsername("abc");
    final String errorMessage = "error.principalNotFound";
    when(userService.getUser(user.getAlternateId())).thenThrow(new NotFoundException(errorMessage, (Object[])null));
    
    final MvcResult result = this.mockMvc.perform(get(UserController.USER_MAPPING, user.getAlternateId())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(ExceptionController.EXCEPTION_ERROR_VIEW))
      .andReturn();
    
    assertThat(result.getResponse().getContentAsString(),
        containsString(messageSource.getMessage(errorMessage, (Object[])null, Locale.getDefault())));
  }
  
  @Test
  public void whenUserDetailsThrowsException_thenErrorViewReturned() throws Exception {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUsername("abc");
    when(userService.getUser(user.getAlternateId())).thenThrow(new NullPointerException());
    
    final MvcResult result = this.mockMvc.perform(get(UserController.USER_MAPPING, user.getAlternateId())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(ExceptionController.EXCEPTION_ERROR_VIEW))
      .andReturn();
    
    assertThat(result.getResponse().getContentAsString(),
        containsString(messageSource.getMessage(ExceptionController.UNKNOWN_ERROR_PROPERTY, (Object[])null, Locale.getDefault())));
  }
  
  @Test
  public void whenGetUserForUpdate_thenEditUserPageReturned() throws Exception {
    final Role role = new Role(RoleName.USER);
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setAccountEnabled(true);
    user.setEmail("abc@134.com");
    user.setFirstName("John");
    user.setLastName("Smith");
    user.setRoles(roles);
    user.setTimeZone(TimeZone.getDefault());
    user.setUsername("johnsmith");
    
    when(userService.getUser(user.getAlternateId())).thenReturn(user);
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, user.getAlternateId())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param(UserController.ACTION_REQUEST_PARAM, "new"))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.EDIT_USER_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
      .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())));
    
    verify(userService, times(1)).getUser(user.getAlternateId());
  }
  
  @Test
  public void whenBindingResultHasErrors_thenNewUserReturnsNewUserViewViewAndErrorMessagePresent() throws Exception{
    final UserDto userDto = buildUserDto();
    userDto.setEmail(null);
    
    this.mockMvc.perform(post(UserController.USERS_MAPPING)
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername()))
        .andExpect(view().name(UserController.NEW_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
  }
  
  @Test
  public void whenNewUserThrowsApplicationException_thenNewUserReturnsNewUserViewViewAndErrorMessagePresent() throws Exception{
    final UserDto userDto = buildUserDto();
    final String errorMessage = "not found";
    
    when(userService.save(any(UserDto.class))).thenThrow(new NotFoundException(errorMessage, (Object[])null));
    
    this.mockMvc.perform(post(UserController.USERS_MAPPING)
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername()))
        .andExpect(view().name(UserController.NEW_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo(errorMessage)));
    
    verify(userService, times(1)).save(any(UserDto.class));
  }
  
  @Test
  public void whenNewUserCreated_thenUserDetailsPagePresented() throws Exception{
    final UserDto userDto = buildUserDto();
    final User user = new User();
    user.setId(Long.valueOf(1));
    
    when(userService.save(any(UserDto.class))).thenReturn(user);
    
    final MvcResult result = this.mockMvc.perform(post(UserController.USERS_MAPPING)
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(user.getAlternateId()).encode().toUriString()));
    
    verify(userService, times(1)).save(any(UserDto.class));
  }
  
  @Test
  public void whenBindingResultHasErrors_thenEditUserReturnsNewUserViewViewAndErrorMessagePresent() throws Exception{
    final UserDto userDto = buildUserDto();
    userDto.setEmail(null);
    userDto.setId(UUID.randomUUID());
    
    when(persistentSecurityUtils.currentUserPasswordMatches(easyRandom.nextObject(String.class))).thenReturn(true);
    
    this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("id", userDto.getId().toString())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername()))
        .andExpect(view().name(UserController.EDIT_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
  }
  
  @Test
  public void whenEditUserThrowsApplicationException_thenNewUserReturnsNewUserViewViewAndErrorMessagePresent() throws Exception {
    final String password = easyRandom.nextObject(String.class);
    final UserDto userDto = buildUserDto();
    userDto.setId(UUID.randomUUID());
    final String errorMessage = "not found";
    
    when(persistentSecurityUtils.currentUserPasswordMatches(password)).thenReturn(true);
    when(userService.save(any(UserDto.class))).thenThrow(new NotFoundException(errorMessage, (Object[])null));
    
    this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("id", userDto.getId().toString())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername())
        .param("reasonForChange", easyRandom.nextObject(String.class))
        .param("password", password))
        .andExpect(view().name(UserController.EDIT_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo(errorMessage)));
    
    verify(userService, times(1)).save(any(UserDto.class));
  }
  
  @Test
  public void whenEditUserUpdated_thenUserDetailsPagePresented() throws Exception {
    final String password = easyRandom.nextObject(String.class);
    final User user = new User();
    final UserDto userDto = buildUserDto();
    userDto.setId(user.getAlternateId());
    
    when(persistentSecurityUtils.currentUserPasswordMatches(password)).thenReturn(true);
    when(userService.save(any(UserDto.class))).thenReturn(user);
    
    final MvcResult result = this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("id", userDto.getId().toString())
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername())
        .param("reasonForChange", easyRandom.nextObject(String.class))
        .param("password", password))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(user.getAlternateId()).encode().toUriString()));
    
    verify(userService, times(1)).save(any(UserDto.class));
  }
  
  @Test
  void whenEditingUser_thenMustProvideReasonAndPassword() throws Exception {
    final String password = easyRandom.nextObject(String.class);
    final User user = new User();
    final UserDto userDto = buildUserDto();
    userDto.setId(user.getAlternateId());
    
    when(persistentSecurityUtils.currentUserPasswordMatches(password)).thenReturn(true);
    when(userService.save(any(UserDto.class))).thenReturn(user);
    
    // reason
    this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("id", userDto.getId().toString())
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername())
        .param("password", password))
        .andExpect(status().isOk())
        .andExpect(view().name(UserController.EDIT_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
    
    // password
    this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("id", userDto.getId().toString())
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername())
        .param("reasonForChange", easyRandom.nextObject(String.class)))
        .andExpect(status().isOk())
        .andExpect(view().name(UserController.EDIT_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
    
    when(persistentSecurityUtils.currentUserPasswordMatches(password)).thenReturn(false);
    // incorrect password
    this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR))))
        .param("id", userDto.getId().toString())
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0).name())
        .param("timeZone", userDto.getTimeZone().getID())
        .param("username", userDto.getUsername())
        .param("password", password + "a")
        .param("reasonForChange", easyRandom.nextObject(String.class)))
        .andExpect(status().isOk())
        .andExpect(view().name(UserController.EDIT_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
  }
  
  @Test
  public void whenNotAuthenticated_thenUserHistoryRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USER_HISTORY_MAPPING, Long.valueOf(1)))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenNotAdministrator_thenUserHistoryRedirectsToErrorPage() throws Exception {
    for (int i = 0; i < RoleName.values().length; i++) {
      final RoleName roleName = RoleName.values()[i];
      
      if (roleName != RoleName.ADMINISTRATOR) {
        final MvcResult result = this.mockMvc.perform(get(UserController.USER_HISTORY_MAPPING, Long.valueOf(1))
            .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(roleName)))))
            .andExpect(status().isFound())
            .andReturn();
        
        assertThat(result.getResponse().getRedirectedUrl(), equalTo(NOT_AUTORIZED_MAPPING));
      }
    }
  }
  
  @Test
  public void whenAdministratorRequestsUserHistory_thenUserHistoryReturned() throws Exception {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUsername("abc");
    final List<UserHistory> history = new ArrayList<UserHistory>();
    
    when(userService.getUser(user.getAlternateId())).thenReturn(user);
    when(userHistoryRepository.listUserRevisions(user.getId())).thenReturn(history);
    
    this.mockMvc.perform(get(UserController.USER_HISTORY_MAPPING, user.getAlternateId())
        .with(user(userDetailsBuilder.build(Long.valueOf(1), EMAIL_RANDOMIZER.getRandomValue(), Set.of(RoleName.ADMINISTRATOR)))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USER_HISTORY_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, equalTo(user)))
      .andExpect(model().attribute(UserController.USER_HISTORY_MODEL_ATTRIBUTE, equalTo(history)));
    
    verify(userService, times(1)).getUser(user.getAlternateId());
    verify(userHistoryRepository, times(1)).listUserRevisions(user.getId());
    verifyNoMoreInteractions(userService);
    verifyNoMoreInteractions(userHistoryRepository);
  }
  
  private UserDto buildUserDto() {
    final UserDto user = UserDto.builder()
        .accountEnabled(true)
        .email("jsmith@gmail.com")
        .firstName("John")
        .lastName("Smith")
        .middleInitial("M")
        .roles(List.of(RoleName.USER))
        .timeZone(TimeZone.getTimeZone("Brazil/East"))
        .username("johnsmith")
        .build();

    return user;
  }
}
