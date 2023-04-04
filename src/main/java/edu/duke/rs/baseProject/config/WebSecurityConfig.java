package edu.duke.rs.baseProject.config;

import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter;
import org.springframework.security.web.header.writers.ClearSiteDataHeaderWriter.Directive;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.context.request.RequestContextListener;

import edu.duke.rs.baseProject.error.ApplicationErrorController;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.index.IndexController;
import edu.duke.rs.baseProject.role.PrivilegeName;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AjaxAwareAccessDeniedHandler;
import edu.duke.rs.baseProject.security.AjaxAwareExceptionMappingAuthenticationHandler;
import edu.duke.rs.baseProject.security.AjaxAwareLoginUrlAuthenticationEntryPoint;
import edu.duke.rs.baseProject.security.RestBasicAuthenticationEntryPoint;
import edu.duke.rs.baseProject.user.UserController;
import edu.duke.rs.baseProject.user.UserRestController;

@Configuration
public class WebSecurityConfig {
  private static final long HSTS_AGE_SECONDS = 31536000;
  
  @Configuration
  @Order(1)
  public static class ActuatorConfigurationAdapter {
    static String MANAGEMENT_AUTHORITY = "MANAGEMENT";
    @Value("${app.management.userName}")
    private String managementUserName;
    @Value("${app.management.password}")
    private String managementPassword;
    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;
    
    @Bean
    InMemoryUserDetailsManager actuatorUserDetailsManager() {
      final UserDetails user = User.withUsername(managementUserName)
        .password(passwordEncoder().encode(managementPassword))
        .authorities(MANAGEMENT_AUTHORITY).build();
   
      return new InMemoryUserDetailsManager(user);          
    }
    
    @Bean
    @Order(1)
    SecurityFilterChain actuatorSecurityFilterChain(final HttpSecurity http) throws Exception {
      final AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder
        .userDetailsService(actuatorUserDetailsManager())
        .passwordEncoder(passwordEncoder());
      
      if (sslEnabled) {
        http.headers()
          .httpStrictTransportSecurity()
            .includeSubDomains(true)
            .maxAgeInSeconds(HSTS_AGE_SECONDS);
  
        http.requiresChannel()
          .anyRequest().requiresSecure();
      }
      
      http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
        .securityMatcher(EndpointRequest.toAnyEndpoint())
          .authorizeHttpRequests(authorize -> authorize.anyRequest().hasAuthority(MANAGEMENT_AUTHORITY))
            .exceptionHandling()
              .accessDeniedHandler(accessDeniedHandler())
          .and()
            .csrf().disable()
            .httpBasic()
              .authenticationEntryPoint(restAuthenticationEntryPoint());
     
      return http.build();
    }
  }
  
  @Configuration
  @Order(2)
  public static class ApplicationConfigurationAdapter {
    private static final String LOGIN_PAGE = "/login";
    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;
    @Value("${spring.security.debug:false}")
    boolean securityDebug;
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Bean
    @Order(2)
    public SecurityFilterChain appFilterChain(final HttpSecurity http) throws Exception {
      final AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
      
      if (sslEnabled) {
        http.headers()
          .httpStrictTransportSecurity()
            .includeSubDomains(true)
            .maxAgeInSeconds(HSTS_AGE_SECONDS);  
    
        http.requiresChannel()
          .anyRequest().requiresSecure();
      }
      
      http.headers()
        .xssProtection()
        .and()
        .contentSecurityPolicy("form-action 'self';default-src 'self'; script-src 'self' https://www.google-analytics.com data: 'unsafe-inline';style-src 'self' 'unsafe-inline' fonts.googleapis.com; font-src 'self' fonts.googleapis.com fonts.gstatic.com; img-src 'self' data: fonts.gstatic.com https://www.google-analytics.com; connect-src 'self' fonts.googleapis.com fonts.gstatic.com https://www.google-analytics.com");      
     
      http
        .csrf()
          .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
      
      http
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
        .and()
          .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
            .authenticationEntryPoint(loginUrlAuthenticationEntryPoint())
        .and()
        .authorizeHttpRequests()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .requestMatchers("/", "/error/**", "/webfonts/**", "/img/**", "/login", "/users/pwdreset", "/i18n/**").permitAll()
            .requestMatchers("/monitoring").hasRole(RoleName.ADMINISTRATOR.name())
            .requestMatchers(UserRestController.USERS_MAPPING).hasAuthority(PrivilegeName.VIEW_USERS.name())
            .requestMatchers(UserController.USERS_MAPPING + "/**").hasAuthority(PrivilegeName.EDIT_USERS.name())
            .requestMatchers(HttpMethod.GET, UserController.USERS_MAPPING).hasAuthority(PrivilegeName.VIEW_USERS.name())
            .anyRequest().authenticated()
        .and()
          .formLogin()
            .failureHandler(authenticationFailureHandler())
        .and()
          .logout()
            .clearAuthentication(true)
            .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
            .logoutSuccessUrl(IndexController.INDEX_MAPPING)
            .addLogoutHandler(new HeaderWriterLogoutHandler(new ClearSiteDataHeaderWriter(Directive.CACHE, Directive.COOKIES, Directive.STORAGE)))
            .permitAll()
            .deleteCookies("SESSION")
            .invalidateHttpSession(true);
      
      return http.build();
    }

    @Bean
    static AuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
      return new AjaxAwareLoginUrlAuthenticationEntryPoint(LOGIN_PAGE);
    }
    
    @Bean
    static AuthenticationFailureHandler authenticationFailureHandler() {
      final AjaxAwareExceptionMappingAuthenticationHandler handler = new AjaxAwareExceptionMappingAuthenticationHandler();
      final Map<String, String> failureMap = new HashMap<>();
      
      failureMap.put(AccountExpiredException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=accountExpired");
      failureMap.put(CredentialsExpiredException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=credentialsExpired");
      failureMap.put(DisabledException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=accountDisabled");
      failureMap.put(LockedException.class.getName(), ApplicationErrorController.ERROR_MAPPING + "?error=accountLocked");
      
      handler.setExceptionMappings(failureMap);
      handler.setDefaultFailureUrl(LOGIN_PAGE + "?error=true");
      
      return handler;
    }
  }
  
 
	@Bean
	static AuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new RestBasicAuthenticationEntryPoint();
	}
	
	@Bean
  static AccessDeniedHandler accessDeniedHandler() {
    return new AjaxAwareAccessDeniedHandler(ApplicationErrorController.ERROR_MAPPING + "?error=403", HomeController.HOME_MAPPING);
  }
	
	@Bean
  static PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(10, new SecureRandom());
  }
	
	//used to inject http request
  @Bean 
  RequestContextListener requestContextListener(){
    return new RequestContextListener();
  } 
}
