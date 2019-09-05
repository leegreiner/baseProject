package edu.duke.rs.baseProject.user.passwordReset;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
