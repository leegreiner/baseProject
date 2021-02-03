package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.text.IsEqualCompressingWhiteSpace.equalToCompressingWhiteSpace;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Locale;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import edu.duke.rs.baseProject.AbstractWebTest;
import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.datatables.DataTablesInput;
import edu.duke.rs.baseProject.datatables.DataTablesTestUtils;
import edu.duke.rs.baseProject.datatables.Search;
import edu.duke.rs.baseProject.exception.NotFoundException;
import edu.duke.rs.baseProject.security.ErrorInfo;
import edu.duke.rs.baseProject.util.HttpUtils;

@WebMvcTest(UserRestController.class)
public class UserRestControllerUnitTest extends AbstractWebUnitTest {
  @MockBean
  private UserRepository userRepositoryMock;
  @Autowired
  private MessageSource messageSource;
  
  @Test
  @WithMockUser(username = "test", authorities = { "VIEW_USERS" })
  public void whenNoSearchCriteriaProvided_thenFindAllUsers() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    final String params = DataTablesTestUtils.toRequestParameters(input);

    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(UserRestController.USERS_MAPPING + "?" + params);
    
    when(userRepositoryMock.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<UserListItem>(new ArrayList<UserListItem>()));
    
    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk());
    
    verify(userRepositoryMock, times(1)).findAllBy(any(Pageable.class));
    verifyNoMoreInteractions(userRepositoryMock);
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "VIEW_USERS" })
  public void whenSearchCriteriaProvided_thenSearchCriteriaIsUsed() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    input.setSearch(new Search("abc", false));
    final String params = DataTablesTestUtils.toRequestParameters(input);
    
    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(UserRestController.USERS_MAPPING + "?" + params);
    
    when(userRepositoryMock.findByLastNameStartingWithIgnoreCase(eq(input.getSearch().getValue()), any(Pageable.class)))
      .thenReturn(new PageImpl<UserListItem>(new ArrayList<UserListItem>()));
    
    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk());
    
    verify(userRepositoryMock, times(1)).findByLastNameStartingWithIgnoreCase(eq(input.getSearch().getValue()), any(Pageable.class));
    verifyNoMoreInteractions(userRepositoryMock);
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "VIEW_USERS" })
  public void whenAjaxRequestThrowsApplicationException_thenBadRequestReturned() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    input.setSearch(new Search("abc", false));
    final String params = DataTablesTestUtils.toRequestParameters(input);
    final String errorMessage = "error.principalNotFound";
    
    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(UserRestController.USERS_MAPPING + "?" + params)
        .header(HttpUtils.AJAX_REQUEST_HEADER, HttpUtils.AJAX_REQUEST_HEADER_VALUE);
    
    when(userRepositoryMock.findByLastNameStartingWithIgnoreCase(eq(input.getSearch().getValue()), any(Pageable.class)))
      .thenThrow(new NotFoundException(errorMessage, (Object[])null));
    
    final MvcResult result = mockMvc.perform(requestBuilder)
      .andExpect(status().isBadRequest())
      .andReturn();
    
    final ErrorInfo errorInfo = new ErrorInfo(messageSource.getMessage(errorMessage, (Object[])null,Locale.getDefault()),
        AbstractWebTest.LOCAL_HOST + AbstractWebTest.API + UserController.USERS_MAPPING);
    final ObjectMapper mapper = new ObjectMapper();
    
    assertThat(result.getResponse().getContentAsString(), equalToCompressingWhiteSpace(mapper.writeValueAsString(errorInfo)));
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "VIEW_USERS" })
  public void whenAjaxRequestThrowsException_thenBadRequestReturned() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    input.setSearch(new Search("abc", false));
    final String params = DataTablesTestUtils.toRequestParameters(input);
    final String errorMessage = "Oops";
    
    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(UserRestController.USERS_MAPPING + "?" + params)
        .header(HttpUtils.AJAX_REQUEST_HEADER, HttpUtils.AJAX_REQUEST_HEADER_VALUE);
    
    when(userRepositoryMock.findByLastNameStartingWithIgnoreCase(eq(input.getSearch().getValue()), any(Pageable.class)))
      .thenThrow(new NullPointerException(errorMessage));
    
    final MvcResult result = mockMvc.perform(requestBuilder)
      .andExpect(status().isInternalServerError())
      .andReturn();
    
    final ErrorInfo errorInfo = new ErrorInfo(errorMessage, AbstractWebTest.LOCAL_HOST + AbstractWebTest.API + UserController.USERS_MAPPING);
    final ObjectMapper mapper = new ObjectMapper();
    
    assertThat(result.getResponse().getContentAsString(), equalToCompressingWhiteSpace(mapper.writeValueAsString(errorInfo)));
  }
  
  @Test
  @WithMockUser(username = "test")
  public void whenNotAuthorized_thenForbidden() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    input.setSearch(new Search("abc", false));
    final String params = DataTablesTestUtils.toRequestParameters(input);
    
    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(UserRestController.USERS_MAPPING + "?" + params)
        .header(HttpUtils.AJAX_REQUEST_HEADER, HttpUtils.AJAX_REQUEST_HEADER_VALUE);
    
    mockMvc.perform(requestBuilder)
      .andExpect(status().isForbidden());
  }
}
