package edu.duke.rs.baseProject.config;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import edu.duke.rs.baseProject.user.passwordReset.ExpirePasswordChangeIdsJob;

@Configuration
@Profile("!test")
public class QuartzConfig {
  private transient final JobProperties jobProperties;
  
  public QuartzConfig(final JobProperties jobProperties) {
    this.jobProperties = jobProperties;
  }
  
  @Bean
  @Profile("!samlSecurity")
  public JobDetail expirePasswordChangeIdsDetail() {
    return JobBuilder
        .newJob(ExpirePasswordChangeIdsJob.class)
        .withIdentity("ExpirePasswordChangeIdsJob")
        .storeDurably()
        .build();
  }

  @Bean
  @Profile("!samlSecurity")
  public Trigger expirePasswordChangeIdsJobTrigger(JobDetail expirePasswordChangeIdsDetail) {
    return TriggerBuilder
        .newTrigger()
        .forJob(expirePasswordChangeIdsDetail)
        .withIdentity("ExpirePasswordChangeIdsJobTrigger")
        .withSchedule(CronScheduleBuilder.cronSchedule(jobProperties.getExpirePasswordChangeIdsCronSchedule()))
        .build();
  }
}
