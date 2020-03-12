package edu.duke.rs.baseProject.security;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.audit.AuditEvent;
import org.springframework.boot.actuate.audit.listener.AuditApplicationEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AuthenticationAttemptListener {
  private static final String DELIMITER = ":";
  private List<String> ignoredPrincipals = new ArrayList<String>(List.of("anonymousUser"));
  
  public AuthenticationAttemptListener(@Value("${app.management.userName}") final String managementUserName) {
    ignoredPrincipals.add(managementUserName);
  }
  
  @EventListener
  public void authenticationAttempted(final AuditApplicationEvent auditApplicationEvent) {
    final AuditEvent event = auditApplicationEvent.getAuditEvent();
    
    if (ignoredPrincipals.contains(event.getPrincipal())) {
      // ignore management user login requests as admin server logs in frequently
      return;
    }
    
    final StringBuffer message = new StringBuffer("Authentication event" + DELIMITER + event.getType()
      + DELIMITER + event.getPrincipal());
    
    final Object details = event.getData().get("details");
    
    if (details != null && details instanceof WebAuthenticationDetails) {
      message.append(DELIMITER + ((WebAuthenticationDetails) details).getRemoteAddress() + 
          DELIMITER + ((WebAuthenticationDetails) details).getSessionId());
    }
    
    log.info(message.toString());
  }
}
