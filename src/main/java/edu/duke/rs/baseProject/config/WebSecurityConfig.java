package edu.duke.rs.baseProject.config;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.saml.SAMLAuthenticationProvider;
import org.springframework.security.saml.SAMLDiscovery;
import org.springframework.security.saml.SAMLEntryPoint;
import org.springframework.security.saml.SAMLLogoutFilter;
import org.springframework.security.saml.SAMLLogoutProcessingFilter;
import org.springframework.security.saml.SAMLProcessingFilter;
import org.springframework.security.saml.key.KeyManager;
import org.springframework.security.saml.metadata.ExtendedMetadata;
import org.springframework.security.saml.metadata.MetadataGenerator;
import org.springframework.security.saml.metadata.MetadataGeneratorFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.channel.ChannelProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.HeaderWriterLogoutHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
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
	public static class ManagementConfigurationAdapter {
		@Value("${app.management.userName}")
		private String managementUserName;
		@Value("${app.management.password}")
		private String managementPassword;
		@Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;
		
		@Bean
		public InMemoryUserDetailsManager userDetailsManager() {
      final UserDetails user = User.withUsername(managementUserName)
        .password(managementPassword)
        .authorities("MANAGEMENT").passwordEncoder(passwordEncoder()::encode).build();
	 
      return new InMemoryUserDetailsManager(user);          
		}

		@Bean
		@Order(1)
		public SecurityFilterChain managementFilterChain(final HttpSecurity http) throws Exception {
		  final AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder
        .userDetailsService(userDetailsManager())
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
        .requestMatcher(EndpointRequest.toAnyEndpoint())
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
        .and()
          .exceptionHandling()
            .accessDeniedHandler(accessDeniedHandler())
        .and()
          .csrf().disable()
          .authorizeRequests().anyRequest().hasAuthority("MANAGEMENT")
        .and()
          .httpBasic()
            .authenticationEntryPoint(restAuthenticationEntryPoint());
      
      return http.build();
		}
	}
  
  @Profile("!samlSecurity")
  @Configuration
  @EnableWebSecurity
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
          .authorizeRequests()
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
            .antMatchers("/", "/error/**", "/webfonts/**", "/img/**", "/login", "/users/pwdreset", "/i18n/**").permitAll()
            .antMatchers("/monitoring").hasRole(RoleName.ADMINISTRATOR.name())
            .antMatchers(UserRestController.USERS_MAPPING).hasAuthority(PrivilegeName.VIEW_USERS.name())
            .antMatchers(UserController.USERS_MAPPING + "/**").hasAuthority(PrivilegeName.EDIT_USERS.name())
            .antMatchers(HttpMethod.GET, UserController.USERS_MAPPING).hasAuthority(PrivilegeName.VIEW_USERS.name())
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
    public static AuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
      return new AjaxAwareLoginUrlAuthenticationEntryPoint(LOGIN_PAGE);
    }
    
    @Bean
    public static AuthenticationFailureHandler authenticationFailureHandler() {
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
  
  @Profile("samlSecurity")
  @Configuration
  public static class SamlWebSecurityConfigurerAdapter {
    @Value("${server.ssl.enabled:false}")
    private boolean sslEnabled;
    @Value("${saml.sp}")
    private String samlAudience;
    @Autowired
    @Qualifier("saml")
    private SavedRequestAwareAuthenticationSuccessHandler samlAuthSuccessHandler;
    @Autowired
    @Qualifier("saml")
    private AuthenticationFailureHandler samlAuthFailureHandler;
    @Autowired
    private SAMLDiscovery samlDiscovery;
    @Autowired
    private SAMLEntryPoint samlEntryPoint;
    @Autowired
    private SAMLLogoutFilter samlLogoutFilter;
    @Autowired
    private SAMLLogoutProcessingFilter samlLogoutProcessingFilter;
    @Autowired
    private SAMLAuthenticationProvider samlAuthenticationProvider;
    @Autowired
    private ExtendedMetadata extendedMetadata;
    @Autowired
    private KeyManager keyManager;

    public MetadataGenerator metadataGenerator() {
      MetadataGenerator metadataGenerator = new MetadataGenerator();
      metadataGenerator.setEntityId(samlAudience);
      metadataGenerator.setExtendedMetadata(extendedMetadata);
      metadataGenerator.setIncludeDiscoveryExtension(false);
      metadataGenerator.setKeyManager(keyManager);
      return metadataGenerator;
    }

    @Bean
    public SAMLProcessingFilter samlWebSSOProcessingFilter(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
      SAMLProcessingFilter samlWebSSOProcessingFilter = new SAMLProcessingFilter();
      samlWebSSOProcessingFilter.setAuthenticationManager(authenticationManager(authenticationConfiguration));
      samlWebSSOProcessingFilter.setAuthenticationSuccessHandler(samlAuthSuccessHandler);
      samlWebSSOProcessingFilter.setAuthenticationFailureHandler(samlAuthFailureHandler);
      return samlWebSSOProcessingFilter;
    }

    @Bean
    public FilterChainProxy samlFilter(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
      List<SecurityFilterChain> chains = new ArrayList<>();
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SSO/**"), samlWebSSOProcessingFilter(authenticationConfiguration)));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/discovery/**"), samlDiscovery));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/login/**"), samlEntryPoint));
      chains.add(new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/logout/**"), samlLogoutFilter));
      chains.add(
          new DefaultSecurityFilterChain(new AntPathRequestMatcher("/saml/SingleLogout/**"), samlLogoutProcessingFilter));
      return new FilterChainProxy(chains);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(final AuthenticationConfiguration authenticationConfiguration) throws Exception {
      return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public MetadataGeneratorFilter metadataGeneratorFilter() {
      return new MetadataGeneratorFilter(metadataGenerator());
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain appFilterChain(final HttpSecurity http, final AuthenticationConfiguration authenticationConfiguration) throws Exception {
      final AuthenticationManagerBuilder authenticationManagerBuilder = http.getSharedObject(AuthenticationManagerBuilder.class);
      authenticationManagerBuilder
        .authenticationProvider(samlAuthenticationProvider);
      
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
        .contentSecurityPolicy("form-action 'self' https://shib.oit.duke.edu;default-src 'self'; script-src 'self' https://www.google-analytics.com data: 'unsafe-inline';style-src 'self' 'unsafe-inline' fonts.googleapis.com; font-src 'self' fonts.googleapis.com fonts.gstatic.com; img-src 'self' data: fonts.gstatic.com https://www.google-analytics.com; connect-src 'self' fonts.googleapis.com fonts.gstatic.com https://www.google-analytics.com");      
        
      http
        .httpBasic()
          .disable()
        .csrf()
          .disable();
      
      http
        .exceptionHandling()
          .accessDeniedHandler(accessDeniedHandler())
          .authenticationEntryPoint(samlEntryPoint);

      http.addFilterBefore(metadataGeneratorFilter(), ChannelProcessingFilter.class)
          .addFilterAfter(samlFilter(authenticationConfiguration), BasicAuthenticationFilter.class)
          .addFilterBefore(samlFilter(authenticationConfiguration), CsrfFilter.class);

      http
        .authorizeRequests()
          .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
          .antMatchers("/", "/error/**", "/webfonts/**", "/img/**", "/login", "/users/pwdreset", "/i18n/**").permitAll()
          .antMatchers("/monitoring").hasRole(RoleName.ADMINISTRATOR.name())
          .antMatchers(UserRestController.USERS_MAPPING).hasAuthority(PrivilegeName.VIEW_USERS.name())
          .antMatchers(UserController.USERS_MAPPING + "/**").hasAuthority(PrivilegeName.EDIT_USERS.name())
          .antMatchers(HttpMethod.GET, UserController.USERS_MAPPING).hasAuthority(PrivilegeName.VIEW_USERS.name())
          .anyRequest().authenticated();

      http.logout().addLogoutHandler((request, response, authentication) -> {
        try {
          response.sendRedirect("/saml/logout");
        } catch (IOException e) {
          e.printStackTrace();
        }
      });
      
      return http.build();
    }
  }
	
	@Bean
	public static AuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new RestBasicAuthenticationEntryPoint();
	}
	
	@Bean
  public static AccessDeniedHandler accessDeniedHandler() {
    return new AjaxAwareAccessDeniedHandler(ApplicationErrorController.ERROR_MAPPING + "?error=403", HomeController.HOME_MAPPING);
  }
	
	@Bean
  public static PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder(10, new SecureRandom());
  }
	
	//used to inject http request
  @Bean 
  public RequestContextListener requestContextListener(){
    return new RequestContextListener();
  } 
}
