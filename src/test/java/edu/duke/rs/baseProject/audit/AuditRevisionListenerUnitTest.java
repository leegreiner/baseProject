package edu.duke.rs.baseProject.audit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import edu.duke.rs.baseProject.security.AppPrincipal;

public class AuditRevisionListenerUnitTest {
  @Mock
  private AppPrincipal appPrincipal;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  private AuditContextHolder auditContextHolder;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenPrincipalNotFound_thenAuditRevisionEntityUserIdNotChanged() {
    final AuditRevisionEntity are = new AuditRevisionEntity();
    when(auditContextHolder.getContext().getCurrentUser()).thenReturn(Optional.empty());
    
    final AuditRevisionListener listener = new AuditRevisionListener();
    
    listener.newRevision(are);
    
    assertThat(are.getUserId(), equalTo(null));
    assertThat(are.getInitiator(), equalTo(AuditRevisionListener.SYSTEM_USER));
  }
  
  @Test
  public void whenPrincipalFoundAndIsAnAppPrincipal_thenAuditRevisionEntityUserIdAndInitiatorSetToUser() {
    final AuditRevisionEntity are = new AuditRevisionEntity();
    when(auditContextHolder.getContext().getCurrentUser()).thenReturn(Optional.of(appPrincipal));
    when(appPrincipal.getUserId()).thenReturn(Long.valueOf(1));
    when(appPrincipal.getDisplayName()).thenReturn("Joe Smith");
    
    final AuditRevisionListener listener = new AuditRevisionListener(auditContextHolder);
    
    listener.newRevision(are);
    
    assertThat(are.getUserId(), equalTo(appPrincipal.getUserId()));
    assertThat(are.getInitiator(), equalTo(appPrincipal.getDisplayName()));
  }
}
