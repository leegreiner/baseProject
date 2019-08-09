package edu.duke.rs.baseProject.user;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.UserDetailsBuilder;
import edu.duke.rs.baseProject.error.ExceptionController;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.login.LoginController;
import edu.duke.rs.baseProject.role.RoleName;

@WebMvcTest(UserController.class)
public class UserControllerUnitTest extends AbstractWebUnitTest {
  @MockBean
  private UserService userService;
  
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
    this.mockMvc.perform(get(UserController.USER_DETAILS_MAPPING, 1L))
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(LOCAL_HOST + LoginController.LOGIN_MAPPING));
  }
  
  @Test
  public void whenAuthenticatedButRequestingNonexistantUser_thenExceptionErrorPageReturned() throws Exception {
    final Long userId = Long.valueOf(1);
    when(userService.getUser(userId)).thenThrow(new NotFoundException("User not found"));
    
    this.mockMvc.perform(get(UserController.USER_DETAILS_MAPPING, userId)
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
    
    this.mockMvc.perform(get(UserController.USER_DETAILS_MAPPING, user.getId())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER))))
      .andExpect(status().isOk())
      .andExpect(view().name(UserController.USER_DETAILS_VIEW))
      .andExpect(model().attribute(UserController.USER_MODEL_ATTRIBUTE, equalTo(user)));
    
    verify(userService, times(1)).getUser(user.getId());
    verifyNoMoreInteractions(userService);
  }
}
