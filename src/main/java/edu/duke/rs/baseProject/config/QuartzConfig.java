package edu.duke.rs.baseProject.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.duke.rs.baseProject.jobs.NoOpJob;

@Configuration
@Profile("!test")
public class QuartzConfig {

  @Bean
  public JobDetail noOpJobDetail() {
    return JobBuilder
        .newJob(NoOpJob.class)
        .withIdentity("NoOpJob")
        .storeDurably()
        .build();
  }

  @Bean Trigger noOpJobTrigger(JobDetail noOpJobDetail) {
    return TriggerBuilder
        .newTrigger()
        .forJob(noOpJobDetail)
        .withIdentity("NoOpJobTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule("0 * * * * ?"))
        .build();
  }
}
