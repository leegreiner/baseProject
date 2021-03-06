package edu.duke.rs.baseProject.user.passwordReset;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.web.util.UriComponentsBuilder;

import edu.duke.rs.baseProject.config.ApplicationProperties;
import edu.duke.rs.baseProject.config.EventConfig;
import edu.duke.rs.baseProject.email.EmailService;
import edu.duke.rs.baseProject.email.MessageType;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
@Setter
@Component
@Profile("!samlSecurity")
public class PasswordResetInititatedProcessor {
  private transient final EmailService emailService;
  private String applicationUrl;
  private Long resetPasswordExpirationDays;
  
  public PasswordResetInititatedProcessor(final EmailService emailService, final ApplicationProperties applicationProperties) {
    this.emailService = emailService;
    this.applicationUrl = applicationProperties.getUrl();
    this.resetPasswordExpirationDays = applicationProperties.getSecurity().getPassword().getResetPasswordExpirationDays();
  }
  
  @Async(EventConfig.ASYNC_TASK_EXECUTOR_BEAN)
  @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
  public void onPasswordResetInitiated(final PasswordResetInitiatedEvent event) {
    log.debug("Received PasswordResetInitiatedEvent for {} ",
        () -> (event.getUser()).toString());
    final Map<String, Object> content = new HashMap<String, Object>();
    content.put("user", event.getUser());
    content.put("expireDays", resetPasswordExpirationDays);
    content.put("url",  UriComponentsBuilder.fromHttpUrl(applicationUrl + PasswordResetController.PASSWORD_RESET_INITIATE_MAPPING)
        .queryParam(PasswordResetController.PASSWORD_RESET_ID_REQUEST_PARAM, event.getUser().getPasswordChangeId().toString()).build());
    
    this.emailService.send(MessageType.PASSWORD_RESET_INITIATED, event.getUser().getEmail(),
        "Your DTS password reset/change request", content, null);
  }

}
