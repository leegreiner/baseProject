package edu.duke.rs.baseProject.user;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.log4j.Log4j2;

@Log4j2
@DisallowConcurrentExecution
public class DisableUnusedAccountsJob implements Job {
  private transient final UserService userService;
  
  public DisableUnusedAccountsJob(final UserService userService) {
    this.userService = userService;
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.info("Expiring unused accounts");
    this.userService.disableUnusedAccounts();
  }

}
