package edu.duke.rs.baseProject.config;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CacheEventLogger implements CacheEventListener<Object, Object> {
  @Override
  public void onEvent(CacheEvent<? extends Object, ? extends Object> cacheEvent) {
    log.debug("Cache event = {}, Key = {},  Old value = {}, New value = {}", cacheEvent.getType(),
        cacheEvent.getKey(), cacheEvent.getOldValue(), cacheEvent.getNewValue());
  }
}
