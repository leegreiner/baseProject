package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
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

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TimeZone;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.security.test.context.support.WithMockUser;
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

@WebMvcTest(UserController.class)
public class UserControllerUnitTest extends AbstractWebUnitTest {
  @MockBean
  private UserService userService;
  @Autowired
  private MessageSource messageSource;
  
  @Test
  public void whenNotAuthenticated_thenUsersRedirectsToLogin() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }

  @Test
  public void whenAuthenticated_thenUsersPagePresented() throws Exception {
    this.mockMvc.perform(get(UserController.USERS_MAPPING)
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER))))
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
  public void whenAuthenticatedButRequestingNonexistantUser_thenExceptionErrorPageReturned() throws Exception {
    final Long userId = Long.valueOf(1);
    when(userService.getUser(userId)).thenThrow(new NotFoundException("error.principalNotFound", (Object[])null));
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, userId)
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(ExceptionController.EXCEPTION_ERROR_VIEW))
      .andExpect(model().attribute(ExceptionController.EXCEPTION_MESSAGE_ATTRIBUTE, is(notNullValue())));
    
    verify(userService, times(1)).getUser(userId);
    verifyNoMoreInteractions(userService);
  }
  
  @Test
  public void whenAuthenticated_thenUserDetailsReturned() throws Exception {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUserName("abc");
    when(userService.getUser(user.getId())).thenReturn(user);
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, user.getId())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USER_DETAILS_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, equalTo(user)));
    
    verify(userService, times(1)).getUser(user.getId());
    verifyNoMoreInteractions(userService);
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenUserDetailsThrowsApplicationException_thenErrorViewReturned() throws Exception {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUserName("abc");
    final String errorMessage = "error.principalNotFound";
    when(userService.getUser(user.getId())).thenThrow(new NotFoundException(errorMessage, (Object[])null));
    
    final MvcResult result = this.mockMvc.perform(get(UserController.USER_MAPPING, user.getId())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(ExceptionController.EXCEPTION_ERROR_VIEW))
      .andReturn();
    
    assertThat(result.getResponse().getContentAsString(),
        containsString(messageSource.getMessage(errorMessage, (Object[])null, Locale.getDefault())));
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenUserDetailsThrowsException_thenErrorViewReturned() throws Exception {
    final User user = new User();
    user.setId(Long.valueOf(1));
    user.setUserName("abc");
    when(userService.getUser(user.getId())).thenThrow(new NullPointerException());
    
    final MvcResult result = this.mockMvc.perform(get(UserController.USER_MAPPING, user.getId())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(ExceptionController.EXCEPTION_ERROR_VIEW))
      .andReturn();
    
    assertThat(result.getResponse().getContentAsString(),
        containsString(messageSource.getMessage(ExceptionController.UNKNOWN_ERROR_PROPERTY, (Object[])null, Locale.getDefault())));
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
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
    user.setUserName("johnsmith");
    
    when(userService.getUser(user.getId())).thenReturn(user);
    
    this.mockMvc.perform(get(UserController.USER_MAPPING, user.getId())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param(UserController.ACTION_REQUEST_PARAM, "new"))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.EDIT_USER_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
      .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())));
    
    verify(userService, times(1)).getUser(user.getId());
  }
  
  @Test
  public void whenBindingResultHasErrors_thenNewUserReturnsNewUserViewViewAndErrorMessagePresent() throws Exception{
    final UserDto userDto = buildUserDto();
    userDto.setEmail(null);
    
    this.mockMvc.perform(post(UserController.USERS_MAPPING)
        .with(csrf())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0))
        .param("timeZone", userDto.getTimeZone().getID())
        .param("userName", userDto.getUserName()))
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
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0))
        .param("timeZone", userDto.getTimeZone().getID())
        .param("userName", userDto.getUserName()))
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
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0))
        .param("timeZone", userDto.getTimeZone().getID())
        .param("userName", userDto.getUserName()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(user.getId()).encode().toUriString()));
    
    verify(userService, times(1)).save(any(UserDto.class));
  }
  
  @Test
  public void whenBindingResultHasErrors_thenEditUserReturnsNewUserViewViewAndErrorMessagePresent() throws Exception{
    final UserDto userDto = buildUserDto();
    userDto.setEmail(null);
    userDto.setId(Long.valueOf(1));
    
    this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("id", userDto.getId().toString())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0))
        .param("timeZone", userDto.getTimeZone().getID())
        .param("userName", userDto.getUserName()))
        .andExpect(view().name(UserController.EDIT_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
  }
  
  @Test
  public void whenEditUserThrowsApplicationException_thenNewUserReturnsNewUserViewViewAndErrorMessagePresent() throws Exception{
    final UserDto userDto = buildUserDto();
    userDto.setId(Long.valueOf(1));
    final String errorMessage = "not found";
    
    when(userService.save(any(UserDto.class))).thenThrow(new NotFoundException(errorMessage, (Object[])null));
    
    this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("id", userDto.getId().toString())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0))
        .param("timeZone", userDto.getTimeZone().getID())
        .param("userName", userDto.getUserName()))
        .andExpect(view().name(UserController.EDIT_USER_VIEW))
        .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(UserController.ROLES_MODEL_ATTRIBUTE, not(nullValue())))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo(errorMessage)));
    
    verify(userService, times(1)).save(any(UserDto.class));
  }
  
  @Test
  public void whenEditUserUpdated_thenUserDetailsPagePresented() throws Exception{
    final UserDto userDto = buildUserDto();
    userDto.setId(Long.valueOf(1));
    final User user = new User();
    user.setId(userDto.getId());
    
    when(userService.save(any(UserDto.class))).thenReturn(user);
    
    final MvcResult result = this.mockMvc.perform(put(UserController.USER_MAPPING, userDto.getId())
        .with(csrf())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("email", userDto.getEmail())
        .param("firstName", userDto.getFirstName())
        .param("lastName", userDto.getLastName())
        .param("middleInitial", userDto.getMiddleInitial())
        .param("roles", userDto.getRoles().get(0))
        .param("timeZone", userDto.getTimeZone().getID())
        .param("userName", userDto.getUserName()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(UserController.USER_MAPPING)
            .buildAndExpand(user.getId()).encode().toUriString()));
    
    verify(userService, times(1)).save(any(UserDto.class));
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
