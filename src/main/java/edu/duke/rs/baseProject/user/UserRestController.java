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
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class UserRestController extends BaseRestController {
  private transient final UserRepository userRepository;
  
  public UserRestController(final UserRepository userRepository) {
    this.userRepository = userRepository;
  }
  
  @GetMapping(API_MAPPING + UserController.USERS_MAPPING)
  public DataTablesOutput<UserListItem> searchUsers(@Valid DataTablesInput input) {
    log.debug("In searchUsers(): " + input.toString());
    final List<String> additionalOrders = new ArrayList<String>();
    additionalOrders.add("id");
    
    final Pageable pageable = DataTablesUtils.toPage(input, null, additionalOrders);
    
    final Page<UserListItem> page = StringUtils.isEmpty(input.getSearch().getValue()) ?
        userRepository.findAllBy(pageable) :
        userRepository.findByLastNameStartingWithIgnoreCase(input.getSearch().getValue(), pageable);
         
    return DataTablesUtils.toDataTablesOutput(input, page);
  }
}
