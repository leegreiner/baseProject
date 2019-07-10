package edu.duke.rs.baseProject.jobs;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NoOpJob implements Job {
  public NoOpJob() {
  }

  @Override
  public void execute(JobExecutionContext context) throws JobExecutionException {
    log.debug("NoOpJob running");
  }

}
