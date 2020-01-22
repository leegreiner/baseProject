package edu.duke.rs.baseProject;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class BaseWebControllerUnitTest {
  @Mock
  private MessageSource messageSource;
  @Mock
  private Model model;
  @Mock
  private RedirectAttributes redirectAttributes;
  private BaseWebController baseWebController;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.initMocks(this);
    baseWebController = new TestBaseWebController(messageSource);
  }

  @Test
  public void whenAddErrorMessageCalled_thenErrorAddedToModel() {
    final String code = "123";
    final String message = "abc";
    final Object[] params = new Object[] {"abc"};
    when(messageSource.getMessage(any(), any(), any(), any())).thenReturn(message);
    
    baseWebController.addErrorMessage(model, code, params);
    
    verify(messageSource, times(1)).getMessage(code, params, code, LocaleContextHolder.getLocale());
    verify(model, times(1)).addAttribute(BaseWebController.FLASH_ERROR_MESSAGE, message);
    verifyNoMoreInteractions(messageSource);
    verifyNoMoreInteractions(model);
  }
  
  @Test
  public void whenAddWarningMessageCalled_thenWarningAddedToModel() {
    final String code = "123";
    final String message = "abc";
    final Object[] params = new Object[] {"abc"};
    when(messageSource.getMessage(any(), any(), any(), any())).thenReturn(message);
    
    baseWebController.addWarningMessage(model, code, params);
    
    verify(messageSource, times(1)).getMessage(code, params, code, LocaleContextHolder.getLocale());
    verify(model, times(1)).addAttribute(BaseWebController.FLASH_WARNING_MESSAGE, message);
    verifyNoMoreInteractions(messageSource);
    verifyNoMoreInteractions(model);
  }
  
  @Test
  public void whenAddFeedbackMessageCalled_thenFeedbackAddedToModel() {
    final String code = "123";
    final String message = "abc";
    final Object[] params = new Object[] {"abc"};
    when(messageSource.getMessage(any(), any(), any(), any())).thenReturn(message);
    
    baseWebController.addFeedbackMessage(model, code, params);
    
    verify(messageSource, times(1)).getMessage(code, params, code, LocaleContextHolder.getLocale());
    verify(model, times(1)).addAttribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, message);
    verifyNoMoreInteractions(messageSource);
    verifyNoMoreInteractions(model);
  }
  
  @Test
  public void whenAddErrorMessageCalled_thenErrorAddedToRedirectAttributes() {
    final String code = "123";
    final String message = "abc";
    final Object[] params = new Object[] {"abc"};
    when(messageSource.getMessage(any(), any(), any(), any())).thenReturn(message);
    
    baseWebController.addErrorMessage(redirectAttributes, code, params);
    
    verify(messageSource, times(1)).getMessage(code, params, code, LocaleContextHolder.getLocale());
    verify(redirectAttributes, times(1)).addFlashAttribute(BaseWebController.FLASH_ERROR_MESSAGE, message);
    verifyNoMoreInteractions(messageSource);
    verifyNoMoreInteractions(redirectAttributes);
  }
  
  @Test
  public void whenAddWarningMessageCalled_thenWarningAddedToRedirectAttributes() {
    final String code = "123";
    final String message = "abc";
    final Object[] params = new Object[] {"abc"};
    when(messageSource.getMessage(any(), any(), any(), any())).thenReturn(message);
    
    baseWebController.addWarningMessage(redirectAttributes, code, params);
    
    verify(messageSource, times(1)).getMessage(code, params, code, LocaleContextHolder.getLocale());
    verify(redirectAttributes, times(1)).addFlashAttribute(BaseWebController.FLASH_WARNING_MESSAGE, message);
    verifyNoMoreInteractions(messageSource);
    verifyNoMoreInteractions(redirectAttributes);
  }
  
  @Test
  public void whenAddFeedbackMessageCalled_thenFeedbackAddedToRedirectAttributes() {
    final String code = "123";
    final String message = "abc";
    final Object[] params = new Object[] {"abc"};
    when(messageSource.getMessage(any(), any(), any(), any())).thenReturn(message);
    
    baseWebController.addFeedbackMessage(redirectAttributes, code, params);
    
    verify(messageSource, times(1)).getMessage(code, params, code, LocaleContextHolder.getLocale());
    verify(redirectAttributes, times(1)).addFlashAttribute(BaseWebController.FLASH_FEEDBACK_MESSAGE, message);
    verifyNoMoreInteractions(messageSource);
    verifyNoMoreInteractions(redirectAttributes);
  }
  
  @Test
  public void whenPathPassed_thenRedirectPrependedToPath() {
    final String path = "/abc";
    
    assertThat(baseWebController.createRedirectViewPath(path), equalTo(BaseWebController.REDIRECT_PREFIX + path));
  }
}
