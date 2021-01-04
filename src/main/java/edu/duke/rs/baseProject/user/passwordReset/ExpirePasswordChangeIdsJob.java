package edu.duke.rs.baseProject.user.passwordReset;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@DisallowConcurrentExecution
public class ExpirePasswordChangeIdsJob implements Job {
  private transient final PasswordResetService passwordResetService;
  
  public ExpirePasswordChangeIdsJob(final PasswordResetService passwordResetService) {
    this.passwordResetService = passwordResetService;
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.debug("Expiring password reset ids");
    this.passwordResetService.expirePasswordResetIds();
  }

}
