package edu.duke.rs.baseProject.config;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import lombok.Getter;
import lombok.Setter;

@Component
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@Validated
public class ApplicationProperties {
  @NotBlank
  private String defaultEmailFrom;
  @NotBlank
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
    @NotNull
    private Integer startupValidationInterval;
    @NotNull
    private Integer startupValidationTimeout;
  }
  
  @Getter
  @Setter
  public static class Google {
    @NotBlank
    private String analysicsTrackingId;
  }
  
  @Getter
  @Setter
  public static class Jobs {
    @NotBlank
    private String disableUnusedAccountsCronSchedule;
    @NotBlank
    private String expirePasswordChangeIdsCronSchedule;
  }
  
  @Getter
  @Setter
  public static class Management {
    @NotBlank
    private String username;
    @NotBlank
    private String password;
  }
  
  @Getter
  @Setter
  public static class Security {
    @NotNull
    private Long disableUnusedAccountsGreaterThanMonths;
    @NotNull
    private Integer numberOfLoginAttemptFailuresBeforeTemporaryLock;
    @NotNull
    private Integer temporaryLockSeconds;
    private Password password = new Password();
    
    @Getter
    @Setter
    public static class Password {
      @NotNull
      private Integer minLength;
      @NotNull
      private Integer maxLength;
      @NotNull
      private Integer numberLowerCase;
      @NotNull
      private Integer numberUpperCase;
      @NotNull
      private Integer numberDigits;
      @NotNull
      private Integer numberSpecial;
      @NotNull
      private Long expirationDays;
      @NotNull
      private Integer historySize;
      @NotNull
      private Long resetPasswordExpirationDays;
    }
  }
  
  @Getter
  @Setter
  public static class Kubernetes {
    @NotBlank
    private String namespace;
    @NotBlank
    private String hazelcastDiscoveryService;
  }
  
  @Getter
  @Setter
  public static class Hazelcast {
    @NotBlank
    private String instanceName;
    @NotNull
    private Integer multicastPort;
    @NotNull
    private Integer bruteForceTtlSeconds;
    @NotNull
    private Integer bruteForceSize;
  }
}
