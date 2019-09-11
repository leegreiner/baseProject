package edu.duke.rs.baseProject.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;

import edu.duke.rs.baseProject.config.EventConfig;
import edu.duke.rs.baseProject.email.EmailService;
import edu.duke.rs.baseProject.email.MessageType;
import edu.duke.rs.baseProject.event.CreatedEvent;
import edu.duke.rs.baseProject.user.passwordReset.PasswordResetController;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Setter
@Component
@Profile("!samlSecurity")
public class NewUserCreatedEventProcessor {
  public static final String USER_NAME_KEY = "userName";
  public static final String EXPIRE_DAYS_KEY = "expireDays";
  public static final String URL_KEY = "url";
  private transient final EmailService emailService;
  @Value("${app.url}")
  private String applicationUrl;
  @Value("${app.resetPasswordExpirationDays}")
  private Long resetPasswordExpirationDays;
  
  public NewUserCreatedEventProcessor(final EmailService emailService) {
    this.emailService = emailService;
  }
  
  @Async(EventConfig.ASYNC_TASK_EXECUTOR_BEAN)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserCreated(final CreatedEvent<User> event) {
    final User user = (User) event.getSource();
    log.debug("Received CreatedEvent for " + user.toString());
    final Map<String, Object> content = new HashMap<String, Object>();
    
    content.put(USER_NAME_KEY, user.getUserName());
    content.put(EXPIRE_DAYS_KEY, resetPasswordExpirationDays);
    content.put(URL_KEY,  UriComponentsBuilder.fromHttpUrl(applicationUrl + PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .queryParam(PasswordResetController.PASSWORD_RESET_ID_REQUEST_PARAM, user.getPasswordChangeId().toString()).build());
    
    this.emailService.send(MessageType.NEW_USER, user.getEmail(),
        "Your Duke Reading Center Data Transmission System(DTS) account has been created", content);
  }
}