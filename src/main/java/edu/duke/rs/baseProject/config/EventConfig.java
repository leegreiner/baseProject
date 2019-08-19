package edu.duke.rs.baseProject.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
@EnableAsync
public class EventConfig {
  public static final String ASYNC_TASK_EXECUTOR_BEAN = "asyncTaskExecutor";
  
  @Bean(ASYNC_TASK_EXECUTOR_BEAN)
  public TaskExecutor getAsyncTaskExecutor() {
    final ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
    executor.setCorePoolSize(20);
    executor.setMaxPoolSize(1000);
    executor.setAwaitTerminationSeconds(10);
    executor.setThreadNamePrefix("Async-");
    
    return executor;
  }
}
