package edu.duke.rs.baseProject.user;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import edu.duke.rs.baseProject.BaseWebController;
import edu.duke.rs.baseProject.exception.ApplicationException;
import edu.duke.rs.baseProject.role.Role;
import edu.duke.rs.baseProject.user.history.UserHistory;
import edu.duke.rs.baseProject.user.history.UserHistoryRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class UserController extends BaseWebController {
	public static final String USERS_VIEW =  "/users/users";
	public static final String USER_DETAILS_VIEW = "/users/userDetails";
	public static final String EDIT_USER_VIEW = "/users/editUser";
	public static final String NEW_USER_VIEW = "/users/newUser";
	public static final String USERS_MAPPING = "/users";
	public static final String USER_MAPPING = USERS_MAPPING + "/{userId}";
	public static final String USER_HISTORY_MAPPING = USER_MAPPING + "/history";
	public static final String USER_HISTORY_VIEW = USERS_MAPPING + "/history/userHistory";
	public static final String USER_MODEL_ATTRIBUTE = "user";
	public static final String USER_HISTORY_MODEL_ATTRIBUTE = "history";
	public static final String ROLES_MODEL_ATTRIBUTE = "roles";
	public static final String ACTION_REQUEST_PARAM = "action";
	private transient final UserService userService;
	private transient final UserHistoryRepository userHistoryRepository;
	
	public UserController(final UserService userService, final UserHistoryRepository userHistoryRepository) {
	  this.userService = userService;
	  this.userHistoryRepository = userHistoryRepository;
	}
	
	@GetMapping(USERS_MAPPING)
	public String getUsers(@RequestParam(name = ACTION_REQUEST_PARAM, required=false) final String action,
	    final Model model) {
	  log.debug("In getUsers()");
	  final boolean newUser = StringUtils.isBlank(action) ? false : true;
	  
	  if (newUser) {
	    loadModelForNewOrEdit(model, null);
	  }
	  
		return newUser ? NEW_USER_VIEW : USERS_VIEW;
  }
	
	@GetMapping(USER_MAPPING)
	public String getUserDetails(@PathVariable("userId") final Long userId,
	    @RequestParam(name = ACTION_REQUEST_PARAM, required=false) String action, Model model) {
	  log.debug("In getUserDetails: " + userId);
	  final boolean editingUser = StringUtils.isBlank(action) ? false : true;
	  final User user = this.userService.getUser(userId);
	  
	  if (editingUser) {
	    loadModelForNewOrEdit(model, user);
	  } else {
	    model.addAttribute(USER_MODEL_ATTRIBUTE, user);
	  }
	  
	  return editingUser ? EDIT_USER_VIEW : USER_DETAILS_VIEW;
	}
	
	@PostMapping(USERS_MAPPING)
  public String newUser(@Valid @ModelAttribute(name = USER_MODEL_ATTRIBUTE) final UserDto user,
      final BindingResult result, final Model model, final RedirectAttributes attributes) {
    if (result.hasErrors()) {
      this.addErrorMessage(model, "error.pleaseCorrectErrors", (Object[])null);
      addRolesToModel(model);
      return NEW_USER_VIEW;
    }
    
    User newUser;
    
    try {
      newUser = this.userService.save(user);
    } catch (final ApplicationException ae) {
      this.addErrorMessage(model, ae.getMessage(), ae.getMessageArguments());
      addRolesToModel(model);
      return NEW_USER_VIEW;
    }
    
    this.addFeedbackMessage(attributes, "message.user.created", (Object[])null);
    
    return UriComponentsBuilder.fromPath(REDIRECT_PREFIX + USER_MAPPING)
        .buildAndExpand(newUser.getId()).encode().toUriString();
  }
	
	@PutMapping(USER_MAPPING)
	public String updateUser(@PathVariable("userId") final Long userId,
	    @Valid @ModelAttribute(name = USER_MODEL_ATTRIBUTE) final UserDto user,
	    final BindingResult result, final Model model, final RedirectAttributes attributes) {
	  if (result.hasErrors()) {
      this.addErrorMessage(model, "error.pleaseCorrectErrors", (Object[])null);
      addRolesToModel(model);
      return EDIT_USER_VIEW;
    }
	  
	  try {
	    this.userService.save(user);
	  } catch (final ApplicationException ae) {
	    this.addErrorMessage(model, ae.getMessage(), ae.getMessageArguments());
      addRolesToModel(model);
      return EDIT_USER_VIEW;
	  }
	  
	  this.addFeedbackMessage(attributes, "message.user.updated", (Object[])null);
    
    return UriComponentsBuilder.fromPath(REDIRECT_PREFIX + USER_MAPPING)
        .buildAndExpand(userId).encode().toUriString();
	}
	
	@GetMapping(USER_HISTORY_MAPPING)
  public String getHistory(@PathVariable("userId") final Long userId, Model model) {
    log.debug("In getHistory: " + userId);
    final User user = this.userService.getUser(userId);
    final List<UserHistory> history = this.userHistoryRepository.listUserRevisions(userId);
    
    model.addAttribute(USER_MODEL_ATTRIBUTE, user);
    model.addAttribute(USER_HISTORY_MODEL_ATTRIBUTE, history);
    
    return USER_HISTORY_VIEW;
  }
	
	private void loadModelForNewOrEdit(final Model model, final User user) {  
	  model.addAttribute(USER_MODEL_ATTRIBUTE, toUserDto(user));
	  addRolesToModel(model);
	}
	
	private void addRolesToModel(final Model model) {
	  final List<Role> roles = this.userService.getRoles();
	  final Map<String, String> roleMap = new HashMap<String, String>();
	  
	  roles.stream().forEach(r -> roleMap.put(r.getName().name(), r.getName().getValue()));
	  
	  model.addAttribute(ROLES_MODEL_ATTRIBUTE, roleMap);
	}
	
	private UserDto toUserDto(final User user) {
	  final UserDto.UserDtoBuilder builder = UserDto.builder();
	  
	  if (user != null) {
  	  final List<String> roles = new ArrayList<String>(user.getRoles().size());
  	  user.getRoles().stream().forEach(r -> roles.add(r.getName().name()));
	    
  	  builder
	      .id(user.getId())
        .firstName(user.getFirstName())
        .middleInitial(user.getMiddleInitial())
        .lastName(user.getLastName())
        .email(user.getEmail())
        .timeZone(user.getTimeZone())
        .accountEnabled(user.isAccountEnabled())
        .lastLoggedIn(user.getLastLoggedIn())
        .roles(roles)
        .userName(user.getUserName());
	  }
	  
	  return builder.build();
	}
}
