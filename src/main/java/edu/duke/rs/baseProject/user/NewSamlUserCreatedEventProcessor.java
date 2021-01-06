package edu.duke.rs.baseProject.user;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Profile;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;

import edu.duke.rs.baseProject.config.EventConfig;
import edu.duke.rs.baseProject.email.EmailService;
import edu.duke.rs.baseProject.email.MessageType;
import edu.duke.rs.baseProject.event.CreatedEvent;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Setter
@Component
@Profile("samlSecurity")
public class NewSamlUserCreatedEventProcessor {
  public static final String USER_NAME_KEY = "username";
  public static final String NEW_USER_SUBJECT_CODE = "email.newUser.subject";
  public static final String URL_KEY = "url";
  @Value("${app.url}")
  private String applicationUrl;
  private transient final EmailService emailService;
  private transient final MessageSource messageSource;

  public NewSamlUserCreatedEventProcessor(final EmailService emailService,
      final MessageSource messageSource) {
    this.emailService = emailService;
    this.messageSource = messageSource;
  }
  
  @Async(EventConfig.ASYNC_TASK_EXECUTOR_BEAN)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onUserCreated(final CreatedEvent<User> event) {
    final User user = (User) event.getSource();
    log.debug("Received CreatedEvent for {}", () -> user.toString());
    final Map<String, Object> content = new HashMap<String, Object>();
    
    content.put(USER_NAME_KEY, user.getUsername());
    content.put(URL_KEY,  UriComponentsBuilder.fromHttpUrl(applicationUrl).build());
    
    this.emailService.send(MessageType.NEW_SAML_USER, user.getEmail(),
        messageSource.getMessage(NEW_USER_SUBJECT_CODE, (Object[]) null, LocaleContextHolder.getLocale()), content,
        null);
  }

}
