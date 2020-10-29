package edu.duke.rs.baseProject.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
public class ApplicationProperties {
  private String defaultEmailFrom;
  private String url;
  private DataSource dataSource = new DataSource();
  private Google google = new Google();
  private Jobs jobs = new Jobs();
  private Management management = new Management();
  private Security security = new Security();
  private Kubernetes kubernetes = new Kubernetes();
  private Hazelcast hazelcast = new Hazelcast();
  
  @Getter
  @Setter
  public static class DataSource {
    private Integer startupValidationInterval;
    private Integer startupValidationTimeout;
  }
  
  @Getter
  @Setter
  public static class Google {
    private String analysicsTrackingId;
  }
  
  @Getter
  @Setter
  public static class Jobs {
    private String disableUnusedAccountsCronSchedule;
    private String expirePasswordChangeIdsCronSchedule;
  }
  
  @Getter
  @Setter
  public static class Management {
    private String username;
    private String password;
  }
  
  @Getter
  @Setter
  public static class Security {
    private Long disableUnusedAccountsGreaterThanMonths;
    private Integer numberOfLoginAttemptFailuresBeforeTemporaryLock;
    private Integer temporaryLockSeconds;
    private Password password = new Password();
    
    @Getter
    @Setter
    public static class Password {
      private Long passwordExpirationDays;
      private Long resetPasswordExpirationDays;
      private Integer minLength;
      private Integer maxLength;
      private Integer numberLowerCase;
      private Integer numberUpperCase;
      private Integer numberDigits;
      private Integer numberSpecial;
    }
  }
  
  @Getter
  @Setter
  public static class Kubernetes {
    private String namespace;
    private String hazelcastDiscoveryService;
  }
  
  @Getter
  @Setter
  public static class Hazelcast {
    private String instanceName;
    private int multicastPort;
    private int bruteForceTtlSeconds;
    private int bruteForceSize;
  }
}
