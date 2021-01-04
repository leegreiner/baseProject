package edu.duke.rs.baseProject.audit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

public class UserRevisionListenerUnitTest {
  @Mock
  private AppPrincipal appPrincipal;
  @Mock
  private SecurityUtils securityUtils;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenPrincipalNotFound_thenAuditRevisionEntityUserIdNotChanged() {
    final AuditRevisionEntity are = new AuditRevisionEntity();
    when(securityUtils.getPrincipal()).thenReturn(Optional.empty());
    
    final UserRevisionListener listener = new UserRevisionListener();
    
    listener.newRevision(are);
    
    assertThat(are.getUserId(), equalTo(null));
    assertThat(are.getInitiator(), equalTo(UserRevisionListener.SYSTEM_USER));
  }
  
  @Test
  public void whenPrincipalFoundAndIsAnAppPrincipal_thenAuditRevisionEntityUserIdAndInitiatorSetToUser() {
    final AuditRevisionEntity are = new AuditRevisionEntity();
    when(securityUtils.getPrincipal()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getUserId()).thenReturn(Long.valueOf(1));
    when(appPrincipal.getDisplayName()).thenReturn("Joe Smith");
    
    final UserRevisionListener listener = new UserRevisionListener(securityUtils);
    
    listener.newRevision(are);
    
    assertThat(are.getUserId(), equalTo(appPrincipal.getUserId()));
    assertThat(are.getInitiator(), equalTo(appPrincipal.getDisplayName()));
  }
}
