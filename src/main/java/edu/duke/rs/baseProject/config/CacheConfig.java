package edu.duke.rs.baseProject.config;

import java.lang.reflect.Method;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

@Configuration
@EnableCaching
public class CacheConfig {
  @Bean("methodNameAndParametersKeyGenerator")
  public KeyGenerator keyGenerator() {
    return new MethodNameAndParametersKeyGenerator();
  }
  
  private static class MethodNameAndParametersKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object target, Method method, Object... params) {
      return method.getName() + "_"
          + StringUtils.arrayToDelimitedString(params, "_");
    }
  }
}
