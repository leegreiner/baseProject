package edu.duke.rs.baseProject.user;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.quartz.JobExecutionException;

public class DisableUnusedAccountJobUnitTest {
  @Mock
  private UserService userService;
  
  @Before
  public void init() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void whenDisableUnusedAccountsCalled_thenSerivceToDisableUnusedAccountsCalled()
      throws JobExecutionException {
    new DisableUnusedAccountsJob(userService).execute(null);
    
    verify(userService, times(1)).disableUnusedAccounts();
    verifyNoMoreInteractions(userService);
  }

}
