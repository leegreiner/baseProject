package edu.duke.rs.baseProject.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import edu.duke.rs.baseProject.error.ApplicationErrorController;
import edu.duke.rs.baseProject.home.HomeController;
import edu.duke.rs.baseProject.role.RoleName;
import edu.duke.rs.baseProject.security.AjaxAwareAccessDeniedHandler;
import edu.duke.rs.baseProject.security.AjaxAwareExceptionMappingAuthenticationHandler;
import edu.duke.rs.baseProject.security.AjaxAwareLoginUrlAuthenticationEntryPoint;
import edu.duke.rs.baseProject.security.RestBasicAuthenticationEntryPoint;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
  private static final String LOGIN_PAGE = "/loginPage";
  
	@Bean
  public static PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
  }
	
	@Configuration
	@Order(1)
	public static class ManagementConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Value("${app.management.userName}")
		private String managementUserName;
		@Value("${app.management.password}")
		private String managementPassword;
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
	        
	    manager.createUser(User.withUsername(managementUserName)
    		.password(passwordEncoder().encode(managementPassword))
    		.authorities("MANAGEMENT").build());
	        
			 auth
			 	.userDetailsService(manager)
	        	.passwordEncoder(passwordEncoder());
		}
		
		@Override
    protected void configure(final HttpSecurity http) throws Exception {
			http
        .requiresChannel()
        .and()
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
						
		}
	}
	
	@Configuration
	@Order(2)
	public static class ApplicationConfigurationAdapter extends WebSecurityConfigurerAdapter {
		@Autowired
		private UserDetailsService userDetailsService;
		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth
      	.userDetailsService(userDetailsService)
      	.passwordEncoder(passwordEncoder());
	  }
		
		@Override
		protected void configure(HttpSecurity http) throws Exception {
			http
			  .requiresChannel()
			  .and()
  			  .exceptionHandling()
  			    .accessDeniedHandler(accessDeniedHandler())
  			    .authenticationEntryPoint(loginUrlAuthenticationEntryPoint())
			  .and()
  				.authorizeRequests()
  				  .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
  					.antMatchers("/", "/error/**", "/fonts/**", "/img/**", "/loginPage").permitAll()
  					.anyRequest().hasAuthority(RoleName.USER.name())
  			.and()
  				.formLogin()
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/home", true)
						.failureHandler(authenticationFailureHandler())
  			.and()
					.logout()
						.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
						.logoutSuccessUrl("/")
						.permitAll()
						.deleteCookies("SESSION")
		    			.invalidateHttpSession(true);
		}
	}
	
	@Bean
	public static AuthenticationEntryPoint restAuthenticationEntryPoint() {
		return new RestBasicAuthenticationEntryPoint();
	}
	
	@Bean
	public static AuthenticationEntryPoint loginUrlAuthenticationEntryPoint() {
	  return new AjaxAwareLoginUrlAuthenticationEntryPoint(LOGIN_PAGE);
	}
	
	@Bean
	public static AuthenticationFailureHandler authenticationFailureHandler() {
	  final AjaxAwareExceptionMappingAuthenticationHandler handler = new AjaxAwareExceptionMappingAuthenticationHandler();
	  final Map<String, String> failureMap = new HashMap<>();
	  
	  failureMap.put(AccountExpiredException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=accountExpired");
	  failureMap.put(CredentialsExpiredException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=credentialsExpired");
	  failureMap.put(DisabledException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=accountDisabled");
	  failureMap.put(LockedException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=accountLocked");
	  
	  handler.setExceptionMappings(failureMap);
	  handler.setDefaultFailureUrl("/loginPage?error=true");
	  
	  return handler;
	}
	
	@Bean
	public static AccessDeniedHandler accessDeniedHandler() {
	  return new AjaxAwareAccessDeniedHandler(ApplicationErrorController.ERROR_PATH, HomeController.HOME_MAPPING);
	}
}
