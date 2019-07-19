package edu.duke.rs.baseProject.config;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.ExceptionMappingAuthenticationFailureHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import edu.duke.rs.baseProject.error.ApplicationErrorController;
import edu.duke.rs.baseProject.role.RoleName;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
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
				.requestMatcher(EndpointRequest.toAnyEndpoint())
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.NEVER)
				.and()
					.csrf().disable()
					.authorizeRequests().anyRequest().hasAuthority("MANAGEMENT")
				.and()
					.httpBasic()
						.authenticationEntryPoint(authenticationEntryPoint())
					.and()
						.exceptionHandling()
							.accessDeniedHandler(accessDeniedHandler());
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
				.authorizeRequests()
				  .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
					.antMatchers("/", "/error/**", "/fonts/**", "/img/**", "/loginPage").permitAll()
					.anyRequest().hasAuthority(RoleName.USER.name())
					.and()
				.formLogin()
						.loginPage("/loginPage").permitAll()
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
	public static RestAutenticationAccessDeniedHandler accessDeniedHandler() {
		return new RestAutenticationAccessDeniedHandler();
	}
	
	@Bean
	public static RestBasicAuthenticationEntryPoint authenticationEntryPoint() {
		return new RestBasicAuthenticationEntryPoint();
	}
	
	public static class RestAutenticationAccessDeniedHandler implements AccessDeniedHandler {
		@Override
		public void handle(HttpServletRequest request, HttpServletResponse response,
				AccessDeniedException accessDeniedException) throws IOException, ServletException {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
	}

	public static class RestBasicAuthenticationEntryPoint extends BasicAuthenticationEntryPoint {
		@Override
		public void commence(HttpServletRequest request, HttpServletResponse response,
				AuthenticationException authException) throws IOException, ServletException {
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}
		
		@Override
		public void afterPropertiesSet() throws Exception {
			setRealmName("management realm");
			super.afterPropertiesSet();
		}
	}
	
	@Bean
	public static AuthenticationFailureHandler authenticationFailureHandler() {
	  final ExceptionMappingAuthenticationFailureHandler handler = new ExceptionMappingAuthenticationFailureHandler();
	  final Map<String, String> failureMap = new HashMap<>();
	  
	  failureMap.put(AccountExpiredException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=accountExpired");
	  failureMap.put(CredentialsExpiredException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=credentialsExpired");
	  failureMap.put(DisabledException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=accountDisabled");
	  failureMap.put(LockedException.class.getName(), ApplicationErrorController.ERROR_PATH + "?error=accountLocked");
	  
	  handler.setExceptionMappings(failureMap);
	  handler.setDefaultFailureUrl("/loginPage?error=true");
	  
	  return handler;
	}
}
