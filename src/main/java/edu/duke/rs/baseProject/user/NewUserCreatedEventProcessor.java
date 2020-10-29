package edu.duke.rs.baseProject.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;

import edu.duke.rs.baseProject.config.ApplicationProperties;
import edu.duke.rs.baseProject.config.EventConfig;
import edu.duke.rs.baseProject.email.EmailService;
import edu.duke.rs.baseProject.email.MessageType;
import edu.duke.rs.baseProject.event.CreatedEvent;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetController;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Setter
@Component
@Profile("!samlSecurity")
public class NewUserCreatedEventProcessor {
  static final String NEW_USER_SUBJECT_CODE = "email.newUser.subject";
  static final String USER_NAME_KEY = "username";
  static final String EXPIRE_DAYS_KEY = "expireDays";
  static final String URL_KEY = "url";
  private transient final EmailService emailService;
  private transient final MessageSource messageSource;
  private transient final ApplicationProperties applicationProperties;
  
  @Async(EventConfig.ASYNC_TASK_EXECUTOR_BEAN)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserCreated(final CreatedEvent<User> event) {
    final User user = (User) event.getSource();
    log.debug("Received CreatedEvent for " + user.toString());
    final Map<String, Object> content = new HashMap<String, Object>();
    
    content.put(USER_NAME_KEY, user.getUsername());
    content.put(EXPIRE_DAYS_KEY, applicationProperties.getSecurity().getPassword().getResetPasswordExpirationDays());
    content.put(URL_KEY,  UriComponentsBuilder.fromHttpUrl(applicationProperties.getUrl() + PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .queryParam(PasswordResetController.PASSWORD_RESET_ID_REQUEST_PARAM, user.getPasswordChangeId().toString()).build());
    
    this.emailService.send(MessageType.NEW_USER, user.getEmail(),
        messageSource.getMessage(NEW_USER_SUBJECT_CODE, (Object[]) null, LocaleContextHolder.getLocale()), content,
        null);
  }
}
