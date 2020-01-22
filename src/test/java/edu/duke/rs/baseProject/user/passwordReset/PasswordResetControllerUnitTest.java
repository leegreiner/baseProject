package edu.duke.rs.baseProject.user.passwordReset;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasProperty;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.Locale;
import java.util.UUID;

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
import edu.duke.rs.baseProject.exception.ConstraintViolationException;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.index.IndexController;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.SecurityUtils;

@WebMvcTest(PasswordResetController.class)
public class PasswordResetControllerUnitTest extends AbstractWebUnitTest {
  @MockBean
  private PasswordResetService passwordResetService;
  @MockBean
  private SecurityUtils securityUtils;
  @Autowired
  private MessageSource messageSource;
  
  @Test
  public void whenRequestingInitiatePasswordResetPage_thenPasswordInitiateResetPagePresented() throws Exception {
    this.mockMvc.perform(get(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING))
      .andExpect(status().isOk())
      .andExpect(view().name(PasswordResetController.PASSWORD_RESET_INITIATE_VIEW))
      .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()));
  }
  
  @Test
  public void whenRespondingToAPasswordResetEmail_thenPasswordResetPagePresented() throws Exception {
    final UUID uuid = UUID.randomUUID();
    
    this.mockMvc.perform(get(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .param(PasswordResetController.PASSWORD_RESET_ID_REQUEST_PARAM, uuid.toString()))
      .andExpect(status().isOk())
      .andExpect(view().name(PasswordResetController.PASSWORD_RESET_VIEW))
      .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()))
      .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, 
          hasProperty("passwordChangeId", equalTo(uuid))));
  }
  
  @Test
  public void whenBindingResultHasErrors_thenInitiateResetPasswordReturnsUserToInitiateResetPageErrorMessagePresent()
    throws Exception {
    final InitiatePasswordResetDto dto = new InitiatePasswordResetDto();
    
    this.mockMvc.perform(post(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("email", dto.getEmail()))
        .andExpect(view().name(PasswordResetController.PASSWORD_RESET_INITIATE_VIEW))
        .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE, equalTo("Please correct the errors below.")));
  }
  
  @Test
  public void whenUserNotFoundAndInitiatingPasswordReset_thenInitiateResetPasswordReturnsUserToInitiateResetPageErrorMessagePresent()
    throws Exception {
    final InitiatePasswordResetDto dto = new InitiatePasswordResetDto();
    dto.setEmail("abc@123.com");
    
    doThrow(NotFoundException.class).when(this.passwordResetService).initiatePasswordReset(any(InitiatePasswordResetDto.class));
    
    this.mockMvc.perform(post(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("email", dto.getEmail()))
        .andExpect(view().name(PasswordResetController.PASSWORD_RESET_INITIATE_VIEW))
        .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE,
            equalTo((messageSource.getMessage("error.userWithEmailNotFound", (Object[])null, Locale.getDefault())))));
    
    verify(passwordResetService, times(1)).initiatePasswordReset(any(InitiatePasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
  
  @Test
  public void whenConstraintViolationExceptionAndInitiatingPasswordReset_thenInitiateResetPasswordReturnsUserToInitiateResetPageErrorMessagePresent()
    throws Exception {
    final InitiatePasswordResetDto dto = new InitiatePasswordResetDto();
    dto.setEmail("abc@123.com");
    final ConstraintViolationException exception = new ConstraintViolationException("error.unrecoverableException", (Object[])null);
    
    doThrow(exception)
      .when(this.passwordResetService).initiatePasswordReset(any(InitiatePasswordResetDto.class));
    
    this.mockMvc.perform(post(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("email", dto.getEmail()))
        .andExpect(view().name(PasswordResetController.PASSWORD_RESET_INITIATE_VIEW))
        .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE,
            equalTo((messageSource.getMessage(exception.getMessage(), exception.getMessageArguments(), Locale.getDefault())))));
    
    verify(passwordResetService, times(1)).initiatePasswordReset(any(InitiatePasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
  
  @Test
  public void whenNotAuthenticatedAndInitiatingPasswordReset_thenResetPerformedAndIndexPagePresented()
    throws Exception {
    final InitiatePasswordResetDto dto = new InitiatePasswordResetDto();
    dto.setEmail("abc@123.com");
    
    final MvcResult result = this.mockMvc.perform(post(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("email", dto.getEmail()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(IndexController.INDEX_MAPPING).toUriString()));
    
    verify(passwordResetService, times(1)).initiatePasswordReset(any(InitiatePasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
  
  @Test
  public void whenAuthenticatedAndInitiatingPasswordReset_thenResetPerformedAndHomePagePresented()
    throws Exception {
    final InitiatePasswordResetDto dto = new InitiatePasswordResetDto();
    dto.setEmail("abc@123.com");
    
    when(securityUtils.userIsAuthenticated()).thenReturn(true);
    
    final MvcResult result = this.mockMvc.perform(post(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("email", dto.getEmail()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, not(nullValue())))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(HomeController.HOME_MAPPING).toUriString()));
    
    verify(passwordResetService, times(1)).initiatePasswordReset(any(InitiatePasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
  
  @Test
  public void whenBindingResultHasErrors_thenResetPasswordReturnsUserToResetPageErrorMessagePresent()
    throws Exception {
    final PasswordResetDto dto = new PasswordResetDto();
    
    this.mockMvc.perform(put(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("passwordChangeId", dto.getUsername())
        .param("username", dto.getUsername())
        .param("password", dto.getPassword())
        .param("confirmPassword", dto.getConfirmPassword()))
        .andExpect(view().name(PasswordResetController.PASSWORD_RESET_VIEW))
        .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()));
  }
  
  @Test
  public void whenUserNotFoundUsingResetIdAndResettingPassword_thenResetPasswordReturnsUserToResetPageErrorMessagePresent()
    throws Exception {
    final PasswordResetDto dto = new PasswordResetDto();
    dto.setConfirmPassword("abcAbc123");
    dto.setPassword(dto.getConfirmPassword());
    dto.setPasswordChangeId(UUID.randomUUID());
    dto.setUsername("johnSmith");
    
    doThrow(NotFoundException.class).when(this.passwordResetService).processPasswordReset(any(PasswordResetDto.class));
    
    this.mockMvc.perform(put(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("passwordChangeId", dto.getPasswordChangeId().toString())
        .param("username", dto.getUsername())
        .param("password", dto.getPassword())
        .param("confirmPassword", dto.getConfirmPassword()))
        .andExpect(view().name(PasswordResetController.PASSWORD_RESET_VIEW))
        .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE,
            equalTo((messageSource.getMessage("error.userWithPasswordResetIdNotFound", (Object[])null, Locale.getDefault())))));
    
    verify(passwordResetService, times(1)).processPasswordReset(any(PasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
  
  @Test
  public void whenConstraintViolationExceptionAndResettingPassword_thenResetPasswordReturnsUserToInitiateResetPageErrorMessagePresent()
      throws Exception {
    final PasswordResetDto dto = new PasswordResetDto();
    dto.setConfirmPassword("abcAbc123");
    dto.setPassword(dto.getConfirmPassword());
    dto.setPasswordChangeId(UUID.randomUUID());
    dto.setUsername("johnSmith");
    final ConstraintViolationException exception = new ConstraintViolationException("error.unrecoverableException", (Object[])null);
    
    doThrow(exception)
      .when(this.passwordResetService).processPasswordReset(any(PasswordResetDto.class));
    
    this.mockMvc.perform(put(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("passwordChangeId", dto.getPasswordChangeId().toString())
        .param("username", dto.getUsername())
        .param("password", dto.getPassword())
        .param("confirmPassword", dto.getConfirmPassword()))
        .andExpect(view().name(PasswordResetController.PASSWORD_RESET_VIEW))
        .andExpect(model().attribute(PasswordResetController.PASSWORD_RESET_MODEL_ATTRIBUTE, notNullValue()))
        .andExpect(model().attribute(BaseWebController.FLASH_ERROR_MESSAGE,
            equalTo((messageSource.getMessage(exception.getMessage(), exception.getMessageArguments(), Locale.getDefault())))));
    
    verify(passwordResetService, times(1)).processPasswordReset(any(PasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
  
  @Test
  public void whenNotAuthenticatedAndPasswordReset_thenResetPerformedAndIndexPagePresented()
    throws Exception {
    final PasswordResetDto dto = new PasswordResetDto();
    dto.setConfirmPassword("abcAbc123");
    dto.setPassword(dto.getConfirmPassword());
    dto.setPasswordChangeId(UUID.randomUUID());
    dto.setUsername("johnSmith");
    
    final MvcResult result = this.mockMvc.perform(put(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .param("passwordChangeId", dto.getPasswordChangeId().toString())
        .param("username", dto.getUsername())
        .param("password", dto.getPassword())
        .param("confirmPassword", dto.getConfirmPassword()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, 
            equalTo((messageSource.getMessage("message.passwordResetCompleted", (Object[])null, Locale.getDefault())))))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(IndexController.INDEX_MAPPING).toUriString()));
    
    verify(passwordResetService, times(1)).processPasswordReset(any(PasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
  
  @Test
  public void whenAuthenticatedAndPasswordReset_thenResetPerformedAndHomePagePresented()
    throws Exception {
    final PasswordResetDto dto = new PasswordResetDto();
    dto.setConfirmPassword("abcAbc123");
    dto.setPassword(dto.getConfirmPassword());
    dto.setPasswordChangeId(UUID.randomUUID());
    dto.setUsername("johnSmith");
    
    when(securityUtils.userIsAuthenticated()).thenReturn(true);
    
    final MvcResult result = this.mockMvc.perform(put(PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .with(csrf())
        .with(user(UserDetailsBuilder.build(Long.valueOf(1), RoleName.USER)))
        .param("passwordChangeId", dto.getPasswordChangeId().toString())
        .param("username", dto.getUsername())
        .param("password", dto.getPassword())
        .param("confirmPassword", dto.getConfirmPassword()))
        .andExpect(flash().attribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, 
            equalTo((messageSource.getMessage("message.passwordResetCompleted", (Object[])null, Locale.getDefault())))))
        .andReturn();
    
    assertThat(result.getResponse().getRedirectedUrl(),
        equalTo(UriComponentsBuilder.fromPath(HomeController.HOME_MAPPING).toUriString()));
    
    verify(passwordResetService, times(1)).processPasswordReset(any(PasswordResetDto.class));
    verifyNoMoreInteractions(passwordResetService);
  }
}
