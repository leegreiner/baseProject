package edu.duke.rs.baseProject.error;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import javax.servlet.RequestDispatcher;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.duke.rs.baseProject.AbstractWebUnitTest;

@WebMvcTest(ApplicationErrorController.class)
public class ApplicationErrorControllerUnitTest extends AbstractWebUnitTest {
	@Test
	public void whenBadRequest_thenForwardToBadRequestView() throws Exception {
	  final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING)
	      .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(400));
	  
		this.mockMvc.perform(builder)
			.andExpect(status().isOk())
			.andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/400"));
	}
	
	@Test
  public void whenUnauthorized_thenForwardToUnauthorizedView() throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING)
        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(401));
    
    this.mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/401"));
  }
	
	@Test
  public void whenForbidden_thenForwardToForbiddenView() throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING)
        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(403));
    
    this.mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/403"));
  }
	
	@Test
  public void whenNotFound_thenForwardToNotFoundView() throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING)
        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(404));
    
    this.mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/404"));
  }
	
	@Test
  public void whenInternalError_thenForwardToInternalErrorView() throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING)
        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(500));
    
    this.mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/500"));
  }
	
	@Test
  public void whenUnknownError_thenForwardToUnknownErrorView() throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING)
        .requestAttr(RequestDispatcher.ERROR_STATUS_CODE, Integer.valueOf(-1));
    
    this.mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/unknownError"));
  }
	
	@Test
  public void whenErrorNotSetAndErrorViewIsSet_thenForwardTErrorView() throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING)
        .param(ApplicationErrorController.ERROR_VIEW_PARAM, "accountDisabled");
    
    this.mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/accountDisabled"));
  }
	
	@Test
  public void whenErrorNotSet_thenForwardToUnknownErrorView() throws Exception {
    final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.get(ApplicationErrorController.ERROR_MAPPING);
    
    this.mockMvc.perform(builder)
      .andExpect(status().isOk())
      .andExpect(view().name(ApplicationErrorController.BASE_ERROR_VIEW + "/unknownError"));
  }
}
