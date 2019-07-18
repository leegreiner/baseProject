package edu.duke.rs.baseProject.user;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.core.type.TypeReference;

import edu.duke.rs.baseProject.AbstractWebIntegrationTest;
import edu.duke.rs.baseProject.datatables.DataTablesInput;
import edu.duke.rs.baseProject.datatables.DataTablesOutput;
import edu.duke.rs.baseProject.datatables.DataTablesTestUtils;
import edu.duke.rs.baseProject.datatables.Search;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.role.RoleRepository;

public class UserRestControllerIntegrationTest extends AbstractWebIntegrationTest {
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private RoleRepository roleRepository;
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
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
    
    User user1 = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    user1 = userRepository.save(user1);
    User user2 = new User("simmyjohnson", "password", "Simmy", "Johnson","simmyJohnson@gmail.com", roles);
    user2 = userRepository.save(user2);
    User user3 = new User("jimmyjohnson", "password", "Jimmy", "Johnson","jimmyJohnson@gmail.com", roles);
    user3 = userRepository.save(user3);

    final MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
    
    final DataTablesOutput<UserListItem> output =
        mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<DataTablesOutput<UserListItem>>() {});
    
    final List<UserListItem> expectedResult = new ArrayList<UserListItem>();
    Map<String, Object> backingMap = new HashMap<>();
    backingMap.put("id", user2.getId());
    backingMap.put("firstName", user2.getFirstName());
    backingMap.put("lastName", user2.getLastName());
    expectedResult.add(PROJECTION_FACTORY.createProjection(UserListItem.class, backingMap));
    backingMap = new HashMap<String, Object>();
    backingMap.put("id", user3.getId());
    backingMap.put("firstName", user3.getFirstName());
    backingMap.put("lastName", user3.getLastName());
    expectedResult.add(PROJECTION_FACTORY.createProjection(UserListItem.class, backingMap));
    backingMap = new HashMap<String, Object>();
    backingMap.put("id", user1.getId());
    backingMap.put("firstName", user1.getFirstName());
    backingMap.put("lastName", user1.getLastName());
    expectedResult.add(PROJECTION_FACTORY.createProjection(UserListItem.class, backingMap));
    
    assertThat(output.getDraw(), equalTo(input.getDraw()));
    assertThat(output.getRecordsFiltered(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getRecordsTotal(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getData().size(), equalTo(expectedResult.size()));
    
    for (int i = 0; i < output.getData().size(); i++) {
      assertThat(output.getData().get(i).getId(), equalTo(expectedResult.get(i).getId()));
      assertThat(output.getData().get(i).getFirstName(), equalTo(expectedResult.get(i).getFirstName()));
      assertThat(output.getData().get(i).getLastName(), equalTo(expectedResult.get(i).getLastName()));
    }
  }
  
  @Test
  @WithMockUser(username = "test", authorities = { "USER" })
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
    
    User user1 = new User("jimmystevens", "password", "Jimmy", "Stevens","jimmyStevens@gmail.com", roles);
    user1 = userRepository.save(user1);
    User user2 = new User("simmyjohnson", "password", "Simmy", "Johnson","simmyJohnson@gmail.com", roles);
    user2 = userRepository.save(user2);
    User user3 = new User("jimmyjohnson", "password", "Jimmy", "Johnson","jimmyJohnson@gmail.com", roles);
    user3 = userRepository.save(user3);

    final MvcResult result = mockMvc.perform(requestBuilder).andExpect(status().isOk()).andReturn();
    
    final DataTablesOutput<UserListItem> output =
        mapper.readValue(result.getResponse().getContentAsString(), new TypeReference<DataTablesOutput<UserListItem>>() {});
    
    final List<UserListItem> expectedResult = new ArrayList<UserListItem>();
    Map<String, Object> backingMap = new HashMap<>();
    backingMap.put("id", user2.getId());
    backingMap.put("firstName", user2.getFirstName());
    backingMap.put("lastName", user2.getLastName());
    expectedResult.add(PROJECTION_FACTORY.createProjection(UserListItem.class, backingMap));
    backingMap = new HashMap<String, Object>();
    backingMap.put("id", user3.getId());
    backingMap.put("firstName", user3.getFirstName());
    backingMap.put("lastName", user3.getLastName());
    expectedResult.add(PROJECTION_FACTORY.createProjection(UserListItem.class, backingMap));
    
    assertThat(output.getDraw(), equalTo(input.getDraw()));
    assertThat(output.getRecordsFiltered(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getRecordsTotal(), equalTo(Long.valueOf(expectedResult.size())));
    assertThat(output.getData().size(), equalTo(expectedResult.size()));
    
    for (int i = 0; i < output.getData().size(); i++) {
      assertThat(output.getData().get(i).getId(), equalTo(expectedResult.get(i).getId()));
      assertThat(output.getData().get(i).getFirstName(), equalTo(expectedResult.get(i).getFirstName()));
      assertThat(output.getData().get(i).getLastName(), equalTo(expectedResult.get(i).getLastName()));
    }
  }
}
