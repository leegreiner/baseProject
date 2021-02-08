package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.PersistentUserBuilder;
import edu.duke.rs.baseProject.datatables.DataTablesInput;
import edu.duke.rs.baseProject.datatables.DataTablesOutput;
import edu.duke.rs.baseProject.datatables.DataTablesTestUtils;
import edu.duke.rs.baseProject.datatables.Search;
import edu.duke.rs.baseProject.error.ApplicationErrorController;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;

public class UserRestControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Autowired
  private RoleRepository roleRepository;
  @Autowired
  private PersistentUserBuilder persistentUserBuilder;
  
  @Test
  @WithMockUser(username = "test", authorities = { "VIEW_USERS" })
  public void whenNoSearchCriteriaProvided_thenResultsIncludeAllUsers() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    final String params = DataTablesTestUtils.toRequestParameters(input);
  
    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(API + UserController.USERS_MAPPING + "?" + params);
    
    final Role role = roleRepository.save(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    final User user1 = persistentUserBuilder.build("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    final User user2 = persistentUserBuilder.build("simmyjohnson", "password", "Simmy", "Johnson","simmyJohnson@gmail.com", roles);
    final User user3 = persistentUserBuilder.build("jimmyjohnson", "password", "Jimmy", "Johnson","jimmyJohnson@gmail.com", roles);

    final MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
    
    final DataTablesOutput<UserListItem> output =
        mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<DataTablesOutput<UserListItem>>() {});
    
    final List<User> expectedResult = List.of(user2, user3, user1);
    
    assertThat(output.getDraw(), equalTo(input.getDraw()));
    assertThat(output.getRecordsFiltered(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getRecordsTotal(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getData().size(), equalTo(expectedResult.size()));
    
    for (int i = 0; i < output.getData().size(); i++) {
      assertThat(output.getData().get(i).getId(), equalTo(expectedResult.get(i).getAlternateId()));
      assertThat(output.getData().get(i).getFirstName(), equalTo(expectedResult.get(i).getFirstName()));
      assertThat(output.getData().get(i).getLastName(), equalTo(expectedResult.get(i).getLastName()));
    }
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "VIEW_USERS" })
  public void whenSearchCriteriaProvided_thenResultsAreFiltered() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    input.setSearch(new Search("jo", false));
    
    final String params = DataTablesTestUtils.toRequestParameters(input);
    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(API + UserController.USERS_MAPPING + "?" + params);
    
    final Role role = roleRepository.save(new Role(RoleName.USER));
    final Set<Role> roles = new HashSet<Role>();
    roles.add(role);
    
    persistentUserBuilder.build("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    final User user2 = persistentUserBuilder.build("simmyjohnson", "password", "Simmy", "Johnson","simmyJohnson@gmail.com", roles);
    final User user3 = persistentUserBuilder.build("jimmyjohnson", "password", "Jimmy", "Johnson","jimmyJohnson@gmail.com", roles);

    final MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
    
    final DataTablesOutput<UserListItem> output =
        mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<DataTablesOutput<UserListItem>>() {});
    
    final List<User> expectedResult = List.of(user2, user3);
    
    assertThat(output.getDraw(), equalTo(input.getDraw()));
    assertThat(output.getRecordsFiltered(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getRecordsTotal(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getData().size(), equalTo(expectedResult.size()));
    
    for (int i = 0; i < output.getData().size(); i++) {
      assertThat(output.getData().get(i).getId(), equalTo(expectedResult.get(i).getAlternateId()));
      assertThat(output.getData().get(i).getFirstName(), equalTo(expectedResult.get(i).getFirstName()));
      assertThat(output.getData().get(i).getLastName(), equalTo(expectedResult.get(i).getLastName()));
    }
  }
  
  @Test
  @WithMockUser(username = "test")
  public void whenNotAuthorized_thenRedirectedToForbiddenErrorPage() throws Exception {
    final DataTablesInput input = new DataTablesInput();
    input.addColumn("lastName", false, false, "");
    input.addOrder("lastName", true);
    final String params = DataTablesTestUtils.toRequestParameters(input);
  
    final RequestBuilder requestBuilder =
        MockMvcRequestBuilders.get(API + UserController.USERS_MAPPING + "?" + params);

    mockMvc.perform(requestBuilder)
      .andExpect(status().isFound())
      .andExpect(redirectedUrl(ApplicationErrorController.ERROR_MAPPING + "?error=403"));
  }
}

