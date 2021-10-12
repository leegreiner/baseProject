package edu.duke.rs.baseProject.user;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.duke.rs.baseProject.BaseRestController;
import edu.duke.rs.baseProject.datatables.DataTablesInput;
import edu.duke.rs.baseProject.datatables.DataTablesOutput;
import edu.duke.rs.baseProject.datatables.DataTablesUtils;
import io.micrometer.core.annotation.Timed;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.log4j.Log4j2;

@Log4j2
@RestController
@Tag(name = "UserRestController", description = "User administration REST controller")
public class UserRestController extends BaseRestController {
  public static final String USERS_MAPPING = API_MAPPING + UserController.USERS_MAPPING;
  private transient final UserRepository userRepository;
  
  public UserRestController(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  @GetMapping(API_MAPPING + UserController.USERS_MAPPING)
  @Operation(description = "Search for users")
  @Timed(description = "Search users")
  public DataTablesOutput<UserListItem> searchUsers(@Valid DataTablesInput input) {
    log.debug("In searchUsers(): {}", () -> input.toString());
    final List<String> additionalOrders = new ArrayList<String>();
    additionalOrders.add("id");
    
    final Pageable pageable = DataTablesUtils.toPage(input, null, additionalOrders);
    
    final Page<UserListItem> page = StringUtils.hasLength(input.getSearch().getValue()) ?
        userRepository.findByLastNameStartingWithIgnoreCase(input.getSearch().getValue(), pageable) :
        userRepository.findAllBy(pageable);
         
    return DataTablesUtils.toDataTablesOutput(input, page);
  }
}
