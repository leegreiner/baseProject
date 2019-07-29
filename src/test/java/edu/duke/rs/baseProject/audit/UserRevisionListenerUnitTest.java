package edu.duke.rs.baseProject.audit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import edu.duke.rs.baseProject.security.AppPrincipal;
import edu.duke.rs.baseProject.security.SecurityUtils;

@RunWith(PowerMockRunner.class)
@PrepareForTest(SecurityUtils.class)
public class UserRevisionListenerUnitTest {
  @Mock
  private AppPrincipal appPrincipal;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
    mockStatic(SecurityUtils.class);
  }
  
  @Test
  public void whenPrincipalNotFound_thenAuditRevisionEntityUserIdNotChanged() {
    final AuditRevisionEntity are = new AuditRevisionEntity();
    when(SecurityUtils.getPrincipal()).thenReturn(null);
    when(appPrincipal.getUserId()).thenReturn(Long.valueOf(1));
    
    final UserRevisionListener listener = new UserRevisionListener();
    
    listener.newRevision(are);
    
    assertThat(are.getUserId(), equalTo(null));
  }
  
  @Test
  public void whenPrincipalFoundAndIsAnAppPrincipal_thenAuditRevisionEntityUserIdSetToUsersId() {
    final AuditRevisionEntity are = new AuditRevisionEntity();
    when(SecurityUtils.getPrincipal()).thenReturn(appPrincipal);
    when(appPrincipal.getUserId()).thenReturn(Long.valueOf(1));
    
    final UserRevisionListener listener = new UserRevisionListener();
    
    listener.newRevision(are);
    
    assertThat(are.getUserId(), equalTo(appPrincipal.getUserId()));
  }

}
