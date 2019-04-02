package edu.duke.rs.baseProject.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.AccessDeniedException;
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
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
	private static String MGMT_USER_NAME;
	private static String MGMT_PASSWORD;
	
	@Value("${app.management.userName}")
    public void setManagementUserName(final String mgmtUserName) {
    	MGMT_USER_NAME = mgmtUserName;
    }
    
    @Value("${app.management.password}")
    public void setManagementPassword(final String mgmtPassword) {
    	MGMT_PASSWORD = mgmtPassword;
    }

    public static UserDetailsService managementUserDetailsService() throws Exception {
        final InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        manager.createUser(User.withUsername(MGMT_USER_NAME).password(passwordEncoder().encode(MGMT_PASSWORD)).roles("MANAGEMENT").build());
        
        return manager;
    }
	
	@Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Configuration
	@Order(1)
	public static class ManagementConfigurationAdapter extends WebSecurityConfigurerAdapter {		
		@Override
		protected void configure(AuthenticationManagerBuilder auth) throws Exception {
			 auth
			 	.userDetailsService(managementUserDetailsService())
	        	.passwordEncoder(passwordEncoder());
		}
		
		@Override
        protected void configure(final HttpSecurity http) throws Exception {
			http
				.requestMatcher(EndpointRequest.toAnyEndpoint())
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
					.csrf().disable()
					.authorizeRequests().anyRequest().hasRole("MANAGEMENT")
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
					.antMatchers("/css/**", "/fonts/**", "/img/**", "/js/**").permitAll()
					.antMatchers("/", "/error/**").permitAll()		
					.anyRequest().hasRole("USER")
					.and()
				.formLogin()
						.loginPage("/loginPage").permitAll()
						.loginProcessingUrl("/login")
						.defaultSuccessUrl("/home", true)
						.failureUrl("/loginPage?error=loginError")
				.and()
						.logout()
							.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
							.logoutSuccessUrl("/")
							.deleteCookies("JSESSIONID")
			    			.invalidateHttpSession(true)
			    			.permitAll();
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
}
