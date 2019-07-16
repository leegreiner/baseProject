package edu.duke.rs.baseProject.user;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;

import org.junit.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.duke.rs.baseProject.AbstractWebUnitTest;
import edu.duke.rs.baseProject.datatables.DataTablesInput;
import edu.duke.rs.baseProject.datatables.Search;

@WebMvcTest(UserRestController.class)
public class UserRestControllerTest extends AbstractWebUnitTest {
  @MockBean
  private UserRepository userRepositoryMock;
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenNoSearchCriteriaProvided_thenFindAllUsers() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);

    final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(API + UserController.USERS_MAPPING)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsString(input).getBytes())
        .with(csrf());
    
    when(userRepositoryMock.findAllBy(any(Pageable.class))).thenReturn(new PageImpl<UserListItem>(new ArrayList<UserListItem>()));
    
    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk());
    
    verify(userRepositoryMock, times(1)).findAllBy(any(Pageable.class));
    verifyNoMoreInteractions(userRepositoryMock);
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
  public void whenSearchCriteriaProvided_thenSearchCriteriaIsUsed() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    input.setSearch(new Search("abc", false));

    final RequestBuilder requestBuilder = MockMvcRequestBuilders.post(API + UserController.USERS_MAPPING)
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .content(mapper.writeValueAsString(input).getBytes())
        .with(csrf());
    
    when(userRepositoryMock.findByLastNameStartingWithIgnoreCase(eq(input.getSearch().getValue()), any(Pageable.class)))
      .thenReturn(new PageImpl<UserListItem>(new ArrayList<UserListItem>()));
    
    mockMvc.perform(requestBuilder)
      .andExpect(status().isOk());
    
    verify(userRepositoryMock, times(1)).findByLastNameStartingWithIgnoreCase(eq(input.getSearch().getValue()), any(Pageable.class));
    verifyNoMoreInteractions(userRepositoryMock);
  }
}
