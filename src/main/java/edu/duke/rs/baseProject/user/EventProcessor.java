package edu.duke.rs.baseProject.user;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;

import edu.duke.rs.baseProject.config.EventConfig;
import edu.duke.rs.baseProject.event.CreatedEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EventProcessor {  
  @Async(EventConfig.ASYNC_TASK_EXECUTOR_BEAN)
  @TransactionalEventListener
  public void onUserCreated(CreatedEvent<User> createdEvent) {
    log.debug("Received CreatedEvent for " +
        ((User)createdEvent.getSource()).toString());
  }
}
