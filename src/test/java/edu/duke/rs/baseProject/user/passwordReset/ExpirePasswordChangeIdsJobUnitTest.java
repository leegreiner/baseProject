package edu.duke.rs.baseProject.user.passwordReset;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class ExpirePasswordChangeIdsJobUnitTest {
  @Mock
  private PasswordResetService passwordResetService;
  
  @BeforeEach
  public void init() {
    MockitoAnnotations.openMocks(this);
  }
  
  @Test
  public void whenJobExecuted_thenPasswordResetIdsAreCleared() throws Exception {
    new ExpirePasswordChangeIdsJob(passwordResetService).execute(null);
    
    verify(passwordResetService, times(1)).expirePasswordResetIds();
    verifyNoMoreInteractions(passwordResetService);
  }
}
